#!/bin/bash
if [ "$#" -ne 1 ]; then
    echo "Usage: run_checkCleanInstance.sh <path_task.xml>"
    exit
fi
ant -e checkCleanInstance -Darg0=$1