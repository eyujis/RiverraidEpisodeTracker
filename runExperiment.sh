#!/bin/bash

# Flag to track if SIGINT signal is received
interrupted=false

# Function to handle SIGINT signal
function stop_script {
    echo "Stopping the script..."
    interrupted=true
}

# Register the function to handle SIGINT signal
trap stop_script SIGINT

# Loop from 0 to 9
for runNumber in {0..9}; do
    echo "Running with run number: $runNumber"
    ./gradlew run --args="$runNumber"

    # Check if script was interrupted by SIGINT
    if [ "$interrupted" = true ]; then
        echo "Script interrupted."
        exit 0
    fi
done