#!/bin/bash

export LC_ALL="en_US.UTF-8"

execute () 
{
        java -cp bin/:../alevin-fork/ALEVIN.jar:../alevin-fork/MuLaViTo.jar:../alevin-fork/JUNG2.jar:../alevin-fork/junit-4.10.jar $1 $2 $3
}



#If wie got list print out number of tests
if [ "$2" = "list" ]; then
	execute $1 $2
	exit $?;
fi

if [ ! $# -eq 3 ]; then
	echo "Usage:bash start.sh  package.Test-Main-Class StartNumber EndNumber";
	exit 1;
fi

# if everything is ok start the Program
execute $1 $2 $3
if [ $? -ne 0 ]; then
	#if execution fails
	`touch tests_$2_$3.failed`
	exit 1;
fi

# if execution is successful
`touch tests_$2_$3.end`
exit 0;
