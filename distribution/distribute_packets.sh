#!/bin/bash

export LC_ALL="en_US.UTF-8"


#1 user@host
#2 remotedir
#3 localdir
sshfs_connect () 
{
	sshfs $1:$2 $3 
}


#1 path that contains all sshfs dirs
sshfs_disconnect ()
{
	echo "Try to disconnect all sshfs dirs gracefull (may fail)"
	fusermount -u $1/*
}


quit () 
{
	echo "Kill all remaining sshfs instances"
	#TODO: find a better solution for this
	killall sshfs

	exit 0;
}


if [ ! $#  -eq 1  ]; then
	echo "Usage: bash distribute.sh package.TestClassName"
	exit 1;
fi

#0. Path Verification
if ([ ! -f "distribution/hosts.txt" ] || [ ! -f "distribution/path.txt" ] || [ ! -f "distribution/start.sh" ]); then
	echo "You have to start this script from directory where the distribution-directory is located"
	exit 1;
fi

#0.1 Path to ResilientCode on ALL hosts
aPath=`cat distribution/path.txt`
echo "(Realtive) path on all hosts is: $aPath"

#0.2 list of hosts 
hosts=( $(cat distribution/hosts.txt) )

#2. Get total Number of tests and the testname 
tests=`bash distribution/start.sh $1 list`

if [ $? -ne 0 ]; then
	echo "Execution of start.sh failed, see output"
	exit 1;
fi


#3. Set the default packet size
packetSize=5

echo "We have ${#hosts[@]} hosts to distribute and overall $(( $tests+1 )) tests, packet size is $packetSize."
if [ $packetSize -gt $tests ]; then
	echo "The PacketSize is graeter than the, distribution is not necessary, starting local"
	bash distribution/start.sh $1 0 $tests

	exit $?
fi

#3.1 perpare directory
sPath="/tmp/alevin"
mkdir $sPath
chmod 700 $sPath




#4. prepare each host 
for actHost in ${hosts[*]}
do
	#4.1 Connect to host in a screen
	#4.2 start the start_packet.sh with parameters testname controllFile 
	bla=`ssh $actHost "cd $aPath && screen -d -m -S ALEVIN bash distribution/start_packet.sh $1 $aPath/distribution/$actHost"`
	#wait 2 Seconds, so that the client has time to create the directory
	sleep 2
	#connect to the client via sshfs
	mkdir $sPath/$actHost
	sshfs_connect $actHost $aPath/distribution/$actHost $sPath/$actHost
	if [ $? -ne 0  ]; then
		sshfs_disconnect $sPath
		quit
	fi
	
	echo "Node $actHost are successfully prepared"


	#4.3 When we at the End of all tests set it to maximum
	#if [ $actStart -gt $tests ]; then
#		actStart=$tests
#	fi
done

actStart=0
actEnd=$(( $actStart+$packetSize-1 ))

isDone=0
#5. Send jobs to all Clients
#jobfile= start-end
#statusfile= fail, free, new, busy
while :
do
	for actHost in ${hosts[*]}
	do
		if ([ ! -f $sPath/$actHost/status ] || [ ! -f $sPath/$actHost/job ]); then
			echo "Client setup of $actHost failed"
			isDone=1
			break
		fi
	
		#get host status
		stat=`cat $sPath/$actHost/status`
		#When free give the host a new job
		if [ $stat == "free" ]; then
			#if start point f greater than endPoint we are finshed
			if [ $actStart -gt $actEnd ]; then
				isDone=1
				break;
			fi
			echo "$actStart-$actEnd" > $sPath/$actHost/job
			echo "new" > $sPath/$actHost/status
			echo "Startet job $actStart to $actEnd on $actHost"
			actStart=$(( $actStart+$packetSize ))
			actEnd=$(( $actEnd+$packetSize ))
			if [ $actEnd -gt $tests ]; then
			               actEnd=$tests
			fi
			
		fi

		#if one fail, stop 
		if [ $stat == "fail" ]; then
			echo "Client $actHost failed, stop work"
			isDone=1
			break
		fi
	done

	if [ $isDone -eq 1 ]; then
		break;
	fi

	#wait 10 seconds
	sleep 10
done

#wait untel all work is done
for actHost in ${hosts[*]}
do
	echo "Wait until hosts are done"	
	#get host status
	stat=`cat $sPath/$actHost/status`
	while ([ $stat != "free" ] || [ $stat != "fail" ])
	do
		sleep 10
	done
done

echo "All Clients done, closing down"
sshfs_disconnect $sPath
bash distribution/killDistributed.sh
quit
exit 1
