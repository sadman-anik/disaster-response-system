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

# Run JavaFX project
mvn clean javafx:run