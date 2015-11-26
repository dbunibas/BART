#!/bin/bash
if [ "$#" -ne 1 ]; then
    echo "Usage: run_checkCleanInstance.sh <path_task.xml>"
    exit
fi
TASK_FILE=$1
if  [[ $TASK_FILE != "/"* ]] ;
then #Expading relative path
    CURR_PATH=$(pwd) 
    TASK_FILE="$CURR_PATH/$TASK_FILE"
fi
echo "Executing task "$TASK_FILE 
BASEDIR=$(dirname $0)
BUILDSCRIPT=$BASEDIR/build.xml
ant -e -f $BUILDSCRIPT  checkCleanInstance -Darg0=$TASK_FILE