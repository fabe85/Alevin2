#!/bin/bash

export LC_ALL="en_US.UTF-8"

execute () 
{
        java -cp bin/:../alevin-fork/ALEVIN.jar:../alevin-fork/MuLaViTo.jar:../alevin-fork/JUNG2.jar:../alevin-fork/junit-4.10.jar $1 $2 $3
}


#1 ControllDir
prepare ()
{
	mkdir $1
	touch $1/status
	touch $1/job
}


quit ()
{
	echo "Client ended"
	exit $?;
}

clean ()
{
	rm -r $1
}


if [ ! $# -eq 2 ]; then
	echo "Usage:bash start_packet.sh  package.Test-Main-Class controlDir";
	exit 1;
fi

#Prepare the enviroment
prepare $2
sFile="$2/status"
jFile="$2/job"


#ready
echo "free" > $sFile

#Start it
while :
do
	stat=`cat $sFile`
	if [ $stat == "new" ]; then
		echo "busy" > $sFile
		startNumber=`cat $jFile | cut -f1 -d "-"`
		endNumber=`cat $jFile | cut -f2 -d "-"`
		execute $1 $startNumber $endNumber
		
		#execute fails?
		if [ $? -ne 0 ]; then
			echo "fail" > $sFile
			touch tests_$startNumber_$endNumber.failed
			echo "Execution of job $startNumber to $endNumber failed"
			quit
		fi
		
		`touch tests_$startNumber_$endNumber.end`
		echo "free" > $sFile
	fi
	#Now new job, sleep 10 seconds
	sleep 10
done

quit
clean $2
exit 0
