#!/bin/bash

export LC_ALL="en_US.UTF-8"

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

#3. Compute testrange for each host 
perHost=$(( ($tests)/(${#hosts[@]}) ))

echo "We have ${#hosts[@]} hosts to distribute and overall $(( $tests+1 )) tests, wich leads us to approximately $perHost per Host."


actStart=0

#4. for each host (except the last one)
#for host in ${hosts[*]}
for (( c=0 ; c<(${#hosts[@]}-1); c++ ))
do
	#4.1 Connect to host in a screen
	#4.2 start the start.sh with parameters testname startNumber and endNumber
	actHost=${hosts[c]}
	bla=`ssh $actHost "cd $aPath && screen -d -m -S ALEVIN bash distribution/start.sh $1 $actStart $(( $actStart+$perHost-1 ))"`
	echo "$actHost $actStart $(( $actStart+$perHost-1 )): $bla" 
	actStart=$(( $actStart + $perHost ))
done

#5. Because if we devide in bash we get always a down rounded value, we need to be sure that the last one did get really all remaining jobs
bla=`ssh ${hosts[(${#hosts[@]}) -1]} "cd $aPath && screen -d -m -S ALEVIN bash distribution/start.sh $1 $actStart $(( $tests-1 ))"`
echo "${hosts[(${#hosts[@]}) -1]} $actStart $(( $tests-1 )): $bla"
