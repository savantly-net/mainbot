#!/bin/bash

# This script creates or updates a truststore and imports the certificate from argument
# The truststore is used by the Java application to trust the server.
# The truststore is created in the current directory with the name truststore.jks.

# Usage: ./create_truststore.sh <host> <truststore> <truststore_password> <alias>
# Example: ./create_truststore.sh localhost:9200 truststore.jks changeit myalias

# Check if the number of arguments is correct
if [ "$#" -ne 4 ]; then
    echo "Illegal number of parameters"
    echo "Usage: ./create_truststore.sh <host> <truststore> <truststore_password> <alias>"
    exit 1
fi

# Define the host and the truststore details
HOST=$1
TRUSTSTORE=$2
TRUSTSTORE_PASSWORD=$3
ALIAS=$4


# Check if keytool is installed
if ! command -v keytool &> /dev/null; then
    echo "keytool is required but it's not installed. Aborting."
    exit 1
fi

# Check if openssl is installed
if ! command -v openssl &> /dev/null; then
    echo "openssl is required but it's not installed. Aborting."
    exit 1
fi


# Get current directory
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR


# Fetch the certificate using openssl
echo "Fetching certificate from $HOST..."
openssl s_client -connect $HOST -showcerts </dev/null 2>/dev/null | openssl x509 -outform PEM > ca.pem

# Check if openssl succeeded
if [ $? -ne 0 ]; then
    echo "Failed to fetch the certificate from $HOST"
    exit 1
fi

# Create a truststore and import the certificate
echo "Creating truststore and importing certificate..."
keytool -import -trustcacerts -file ca.pem -alias $ALIAS -keystore $TRUSTSTORE -storepass $TRUSTSTORE_PASSWORD -noprompt

# Check if keytool succeeded
if [ $? -ne 0 ]; then
    echo "Failed to create the truststore or import the certificate"
    exit 1
fi

echo "Truststore created successfully."

# Cleanup
rm ca.pem
