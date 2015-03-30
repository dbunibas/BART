#!/bin/bash
if [ "$#" -ne 1 ]; then
    echo "Usage: run.sh <path_task.xml>"
    exit
fi
ant -e run -Darg0=$1