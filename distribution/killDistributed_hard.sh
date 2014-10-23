#!/bin/bash

export LC_ALL="en_US.UTF-8"

if ([ ! -f "distribution/hosts.txt" ]); then
        echo "You have to start this script from directory where the distribution-directory is located"
        exit 1;
fi

hosts=( $(cat distribution/hosts.txt) )


for host in ${hosts[*]}
do
	ret=`ssh  $host "killall screen"`
	echo "$host: $ret"
done

