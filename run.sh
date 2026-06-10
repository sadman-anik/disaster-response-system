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

if lsof -nP -iTCP:9090 -sTCP:LISTEN >/dev/null 2>&1
then
    echo "Port 9090 is already in use by another DRS server process."
    echo "Stop the existing server first, then run ./run.sh again."
    echo ""
    lsof -nP -iTCP:9090 -sTCP:LISTEN
    exit 1
fi

# Run the single launcher. It starts the DRS server on port 9090, then opens JavaFX.
mvn clean javafx:run
