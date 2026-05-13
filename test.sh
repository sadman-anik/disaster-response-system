#!/bin/bash

echo "Running JUnit tests for DRS - Disaster Response System..."
echo ""

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
echo "Starting tests..."
echo ""

mvn clean test

echo ""
echo "JUnit test command finished."