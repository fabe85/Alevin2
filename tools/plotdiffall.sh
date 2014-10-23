#!/bin/bash

LC_ALL="en_US.UTF-8"
algo1=$1;
algo2=$2;

metricNames=(AvAllPathLength AvPathLength Cost CostRevenue PowerConsumption MaxPathLength RatioMappedRevenue RevenueCost RunningNodes SolelyForwardingHops)


execpath=`dirname "$0"`;

for metric in "${metricNames[@]}"; do
  a1=`echo $algo1 | sed -e 's/XXX/'$metric'/g'`
  a2=`echo $algo2 | sed -e 's/XXX/'$metric'/g'`
echo $a1 - $a2;
  $execpath/plotdiff.sh "$a1" "$a2";
done

