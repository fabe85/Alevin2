#!/bin/bash

LC_ALL="en_US.UTF-8"
experiment=$1;
metric=$2;

outdir=`echo "$experiment" | sed -e 's/_out.txt$//'`
filenameprefix=$outdir"/"$metric;
if [ -e "$outdir" ]; then
  rm -f "$filenameprefix".gnuplot;
  rm -f "$filenameprefix".data;
  rm -f "$filenameprefix".sorteddata;
  rm -f "$filenameprefix".png;
  rm -f "$filenameprefix".pdf;
else
  mkdir "$outdir";
fi

# echo "set terminal pdfcairo noenhanced dashed color" >> $filenameprefix".gnuplot";
# echo "set terminal pngcairo noenhanced dashed color size 1200,1200" >> $filenameprefix".gnuplot";
echo "set terminal pngcairo noenhanced dashed color size 350,350" >> $filenameprefix".gnuplot";
echo "set output \""$filenameprefix".png\"" >> $filenameprefix".gnuplot";
# echo "set title '$metric'" >> $filenameprefix".gnuplot";
echo "set xlabel 'number of substrate nodes'" >> $filenameprefix".gnuplot";
echo "set ylabel '$metric' rotate left" >> $filenameprefix".gnuplot";
echo "set border 3" >> $filenameprefix".gnuplot";
echo "set xtics nomirror" >> $filenameprefix".gnuplot";
echo "set ytics nomirror" >> $filenameprefix".gnuplot";
echo -n "plot '"$filenameprefix".sorteddata' notitle with errorbars" >> $filenameprefix".gnuplot";

scenario="";
thisScenario="";

network="";
thisLineNetwork="";

value="0.0";
values=();
sum="0.0";
numOf=0;
avg="0.0";
while read -r i; do

  thisLineNetwork=`echo "$i" | cut -f1 -d "_"  | cut -f2 -d "	"`;

  value=`echo "$i" | cut -f 3`  
  if [ "$value" == "NaN" ]; then
    continue;
  fi
  
  if [ -z $network ] || [ $network != $thisLineNetwork ]; then
    if [ "$network" != "" ]; then
      if [ "$numOf" -eq 0 ]; then
        avg="0";
      else
        avg=`echo "$sum/$numOf" | bc -l`;
      fi

      s=`echo ${values[@]} | sed -e 's/ /,/g'`;
      sd=`Rscript -e "sd(c($s))" | cut -f2 -d' '`;
      values=();
      echo "$network	`printf "%.3f" "$avg"`	$sd" >> $filenameprefix".data";
    fi

    network=$thisLineNetwork;
    sum="0.0";
    numOf=0;

  fi
  
  values[${#values[@]}]="$value";

  if [ "$value" != "NaN" ]; then
    numOf=$((numOf+1));
    sum=`echo "$sum+$value" | bc -l`;
  fi

done < <(grep "^$metric" "$experiment" | sed -e 's/_vbandwidthWeight:10.0_delegationNodesLevel:0_fullKnowledgeNodesLevel:0_partitions:2//' | cut -f 1,2,3,4,5 -d "_" | sort -k 2 -V)

if [ "$numOf" -eq 0 ]; then
  avg="0";
else
  avg=`echo "$sum/$numOf" | bc -l`;
fi

if [ "$network" != "" ]; then
  s=`echo ${values[@]} | sed -e 's/ /,/g'`;
  sd=`Rscript -e "sd(c($s))" | cut -f2 -d' '`;
  echo "$network	`printf "%.3f" "$avg"`	$sd" >> $filenameprefix".data";
else
  echo "0	0.000" >> $filenameprefix".data";
fi

sort -V $filenameprefix".data" -o $filenameprefix".sorteddata";
rm $filenameprefix".data";
gnuplot $filenameprefix".gnuplot";

