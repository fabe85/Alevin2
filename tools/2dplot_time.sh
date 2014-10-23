#!/bin/bash

LC_ALL="en_US.UTF-8"
experiment=$1;
metric="RunningNodes";

filenameprefix=$experiment"/"time;
alphabetarange="";

if [ -n "$2" ]; then
  alphabetarange=".*";

  if [ "$2" != "*" ]; then
    echo "using alpha = $2";
    alpharange=$alpharange"_alpha:$2";
    filenameprefix=$filenameprefix"_alpha"$2;
  fi

  if [ -n "$3" ] && [ "$3" != "*" ]; then
    echo "using beta = $3";
    alphabetarange=$alphabetarange"_beta:$3";
    filenameprefix=$filenameprefix"_beta"$3;
  fi
fi

if [ -e "$experiment" ]; then
  rm -f "$filenameprefix".gnuplot;
  rm -f "$filenameprefix".data;
  rm -f "$filenameprefix".sorteddata;
  rm -f "$filenameprefix".png;
  rm -f "$filenameprefix".pdf;
else
  mkdir "$experiment";
fi

# echo "set terminal pdfcairo noenhanced dashed color" >> $filenameprefix".gnuplot";
# echo "set terminal pngcairo noenhanced dashed color size 1200,1200" >> $filenameprefix".gnuplot";
echo "set terminal pngcairo noenhanced dashed color size 350,350" >> $filenameprefix".gnuplot";
echo "set output \""$filenameprefix".png\"" >> $filenameprefix".gnuplot";
#  echo "set title '$metric'" >> $filenameprefix".gnuplot";
echo "set xlabel 'number of virtual networks'" >> $filenameprefix".gnuplot";
echo "set ylabel 'time [ms]' rotate left" >> $filenameprefix".gnuplot";
echo -n "plot '"$filenameprefix".sorteddata' notitle with lines" >> $filenameprefix".gnuplot";


scenario="";
thisScenario="";

network="";
thisLineNetwork="";

value="0.0";
sum="0.0";
numOf=0;
avg="0.0";
while read -r i; do

  value=`echo "$i" | cut -f 5`
  if [ "$value" == "NaN" ]; then
    continue;
  fi

  thisLineNetwork=`echo "$i" | cut -f2 -d "_"`;
  if [ -z $network ] || [ $network != $thisLineNetwork ]; then
    if [ "$network" != "" ]; then
      avg=`echo "$sum/$numOf" | bc -l`;
      echo "$network	`printf "%.3f" "$avg"`" >> $filenameprefix".data";
    fi

    network=$thisLineNetwork;
    sum="0.0";
    numOf=0;

  fi

  numOf=$((numOf+1));
  sum=`echo "$sum+$value" | bc -l`;

done < <(grep "^$metric	$alphabetarange" "$experiment"_out.txt | sed -e 's/_alpha:[0-9]*\.[0-9]*_beta:[0-9]*\.[0-9]*//' | cut -f 1,2,3,4,5 -d "_" | sort -k 2);


avg=`echo "$sum/$numOf" | bc -l`;
echo "$network	`printf "%.3f" "$avg"`" >> $filenameprefix".data";

sort -V $filenameprefix".data" -o $filenameprefix".sorteddata";
rm $filenameprefix".data";
gnuplot $filenameprefix".gnuplot";

