#!/bin/bash

LC_ALL="en_US.UTF-8"
firstmetric=`basename $1 | cut -d "_" -f 1`;
secondmetric=`basename $2 | cut -d "_" -f 1`;
filenameprefix=`dirname $1`__`dirname $2`__$firstmetric;

if [ "$firstmetric" != "$secondmetric" ]; then
  echo "Warning: metrics differ.";
  exit 1;
fi

rm -f "$filenameprefix".*;

paste "$1".sorteddata "$2".sorteddata | cut -f 1,2,4 > "$filenameprefix".sorteddata;

# echo "set terminal pdfcairo noenhanced dashed color" >> $filenameprefix".gnuplot";
# echo "set terminal pngcairo noenhanced dashed color size 1200,1200" >> $filenameprefix".gnuplot";
echo "set terminal pngcairo noenhanced dashed color size 350,350" >> $filenameprefix".gnuplot";
echo "set output \""$filenameprefix".png\"" >> $filenameprefix".gnuplot";
# echo "set title '$metric'" >> $filenameprefix".gnuplot";
echo "set style fill pattern 2" >> $filenameprefix".gnuplot";
echo "set xlabel 'number of virtual networks'" >> $filenameprefix".gnuplot";
echo "set ylabel '$metric' rotate left" >> $filenameprefix".gnuplot";
echo -n "plot '"$filenameprefix".sorteddata' using 1:2:3 with filledcurve notitle" >> $filenameprefix".gnuplot";


gnuplot $filenameprefix".gnuplot";

