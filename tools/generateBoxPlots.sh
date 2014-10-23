#!/bin/bash

LC_ALL="en_US.UTF-8"
# SYNTAX:
#   $ ./generateBoxPlots.sh <Algorithm output file>
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
#         Michael Till Beck
# Created on: Thu 14. April 2011
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
  VNETDIR="$VNETS"_virtual_networks_boxes
  mkdir $VNETDIR

  # Search for the Experiments with $VNETS virtual networks. This is done by
  # grepping for $VNETS in the second to last field.
  DATASET=$(grep -E "$VNETS[[:blank:]][0-9]+.*[^[:blank:]]" $INFILE)

  for METRIC in $METRICS
  do
    BASENAME="$VNETDIR"/"$METRIC"
    OUTFILE="$BASENAME"_boxes.data
    DATALINES=$(echo "$DATASET" | grep "^$METRIC[[:blank:]]")

    # The data has to be cleaned up a bit.
    # First, extract the third field, as it contains the result for the metric.
    DATALINES=$(echo "$DATALINES" | cut -f3)

    # remove 'NaN' results from the list.
    DATALINES=$(echo "$DATALINES" | grep -v 'NaN')

    echo "$DATALINES" | nl --number-format=ln --no-renumber | unexpand -a > $OUTFILE

    cat > "$BASENAME"_boxes.gnuplot <<EOF
set terminal pdfcairo noenhanced dashed color
set output "$BASENAME.pdf"
set boxwidth 0.9 relative
set style fill solid 1.0
plot "$OUTFILE" using 1:2 title '$METRIC' with boxes
EOF
    gnuplot "$BASENAME"_boxes.gnuplot
  done
done
