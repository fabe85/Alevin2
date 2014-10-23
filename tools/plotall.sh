#!/bin/bash

LC_ALL="en_US.UTF-8"
experiment=$1;
param2d1="*";
param2d2="*";
if [ -n "$2" ]; then
  param2d1=$2;
  
  if [ -n "$3" ]; then
    param2d2=$3;
  fi
fi

metricNames2d=(AvAllPathLength AcceptedVnrRatio RejectedNetworksNumber AvPathLength Cost CostRevenue PowerConsumption MaxPathLength RatioMappedRevenue RevenueCost RunningNodes SolelyForwardingHops BFMessageCounter MessagesPerLink NormalMessageCounter NotifyMessageCounter AverageSentMsgsPerNode NodesUsedSolelyForForwarding ClusterCounter AvSecSpreadDemProv MaxSecSpreadDemProv AvSecSpreadProvDem MaxSecSpreadProvDem Runtime)


execpath=`dirname "$0"`;

#$execpath/2dplot_time.sh "$experiment" "$param2d1" "$param2d2";

for metric in "${metricNames2d[@]}"; do
  $execpath/2dplot.sh "$experiment" "$metric" "$param2d1" "$param2d2";
done

