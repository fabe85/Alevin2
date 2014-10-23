#!/bin/bash

LC_ALL="en_US.UTF-8"
# SYNTAX:
#   $ ./generatePlots.sh <Algorithm output file>
#
# This script takes one parameter - the output file of a VNREAL test run.
# This file is expected to be a tab-delimited collection of values with the
# following fields:
#
#   <Metric Name> <Scenario parameters> <Metric value> <#VNets> <Elapsed Time>
#
# Example:
#
#   Cost	50_10_20_0_0.2_0	975.1650000000002	10	469
#
# This script will then generate graphs for all metrics and all scenario
# parameters.
#
# Author: Andreas Fischer
# Created on: Mo 21. March 2011
#

if [ -z "$1" ]
then
  echo "Error: No input file given!"
  exit 1
elif [ ! -e "$1" ]
then
  echo "Error: Input file does not exist!"
  exit 2
elif [ ! -s "$1" ]
then
  echo "Error: Input file is empty!"
  exit 3
fi

INFILE=$1

# Step 1:
# Separate the data for all metrics into individual files.
# A separate directory is created for each number of virtual networks to be
# mapped. Within that directory, each metric gets its own file with the overall
# network load rho in the first column and the metric value in the second column
# (tab delimited).

NETWORKS=$(cut -f 4 $INFILE | sort | uniq)
METRICS=$(cut -f 1 $INFILE | sort | uniq)

for VNETS in $NETWORKS
do
  VNETDIR="$VNETS"_virtual_networks
  mkdir $VNETDIR

  # Search for the Experiments with $VNETS virtual networks. This is done by
  # grepping for $VNETS in the second to last field.
  DATASET=$(grep -E "$VNETS[[:blank:]][0-9]+.*[^[:blank:]]" $INFILE)

  for METRIC in $METRICS
  do
    BASENAME=$VNETDIR/$METRIC
    OUTFILE=$BASENAME.data
    DATALINES=$(echo "$DATASET" | grep "^$METRIC[[:blank:]]")

    # The data has to be cleaned up a bit.
    # First, extract the second and the third field, as those contain rho and
    # the result for the metric.
    DATALINES=$(echo "$DATALINES" | cut -f2,3)

    # Next, extract rho from the parameter settings.
    # Rho is the fifth parameter in the parameter settings, which are delimited
    # by '_'
    DATALINES=$(echo "$DATALINES" | sed -r -e 's/^[0-9]+_[0-9]+_[0-9]+_[0-9]+_([^_]+)_[0-9]+/\1/')

    # Finally, remove 'NaN' results from the list.
    DATALINES=$(echo "$DATALINES" | grep -v 'NaN')

    echo "$DATALINES" > $OUTFILE

    cat > $BASENAME.gnuplot <<EOF
set xrange [0.0:1.0]
set terminal pdfcairo noenhanced dashed color
set output "$BASENAME.pdf"
plot "$OUTFILE" using 1:2 title '$INFILE - $METRIC'
EOF
    gnuplot $BASENAME.gnuplot
  done
done
