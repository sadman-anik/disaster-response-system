#!/bin/bash

echo "Starting DRS - Disaster Response System..."

# Check Java
if ! command -v java &> /dev/null
then
    echo "Java is not installed or not added to PATH."
    echo "Please install/use JDK 17 first."
    exit 1
fi

# Check Maven
if ! command -v mvn &> /dev/null
then
    echo "Maven is not installed or not added to PATH."
    echo "Install Maven first, then run this script again."
    exit 1
fi

echo "Java version:"
java -version

echo ""
echo "Maven version:"
mvn -version

echo ""
echo "Make sure MySQL Server is running."
echo "The application will use src/main/resources/database.properties for username and password."
echo ""

mkdir -p target

SERVER_LOG="target/drs-server.log"

echo "Building project..."
mvn clean compile || exit 1

if lsof -nP -iTCP:9090 -sTCP:LISTEN >/dev/null 2>&1
then
    echo "Port 9090 is already in use by another DRS server process."
    echo "Stop the existing server first, then run ./run.sh again."
    echo ""
    lsof -nP -iTCP:9090 -sTCP:LISTEN
    exit 1
fi

echo ""
echo "Starting DRS server on localhost:9090..."
mvn -q exec:java -Dexec.mainClass=com.sadman.drs.server.DRSServer > "$SERVER_LOG" 2>&1 &
SERVER_PID=$!

cleanup() {
    if kill -0 "$SERVER_PID" 2>/dev/null
    then
        kill "$SERVER_PID" 2>/dev/null
    fi
}
trap cleanup EXIT

echo "Waiting for DRS server to accept connections..."
for attempt in {1..30}
do
    if grep -q "DRS server is running on port 9090" "$SERVER_LOG" 2>/dev/null
    then
        echo "DRS server is ready."
        echo ""
        mvn javafx:run
        exit $?
    fi

    if ! kill -0 "$SERVER_PID" 2>/dev/null
    then
        echo "DRS server failed to start. Server log:"
        cat "$SERVER_LOG"
        exit 1
    fi

    sleep 1
done

echo "DRS server did not become ready. Server log:"
cat "$SERVER_LOG"
exit 1
