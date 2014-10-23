#!/bin/bash

LC_ALL="en_US.UTF-8"
for file in `ls *.data`; do

  currentX="0";
  x="0.0";
  sumY="0.0";
  minY="0.0";
  maxY="0.0";
  linecounter=0;
  firstX=0;

  echo "parsing $file ...";
  while read -r i; do
    if [ "$i" = "" ]; then
      continue;
    fi

    x=${i%	*};
    y=${i#*	};

    if [ $firstX -eq 0 ]; then
      currentX="$x";
      minY="$y";
      maxY="$y";
      firstX=1;
      true > "$file""_avg";

    else

      if [ "$currentX" != "$x" ]; then

        echo "$currentX	`echo \"$sumY/$linecounter\" | bc -l`	$minY	$maxY" >> "$file""_avg";

        currentX="$x";
        minY="$y";
        maxY="$y";
        sumY="0.0";
        linecounter=0;
      fi
    fi

    sumY=`echo "$sumY+$y" | bc -l`;
    if [ $(echo "$y<$minY" | bc -l) = "1" ]; then
      minY="$y";
    fi
    if [ $(echo "$y>$maxY" | bc -l) = "1" ]; then
      maxY="$y";
    fi
    linecounter=$((linecounter+1));
  done < <(cat "$file" | sort -k 1);

  if [ $firstX -ne 0 ]; then
    echo "$x	`echo \"$sumY/$linecounter\" | bc -l`	$minY	$maxY" >> "$file""_avg";
  fi

  echo "set xrange [0.0:1.0]" > $file"_avg.gnuplot";
  echo "set terminal pdfcairo noenhanced dashed color" >> $file"_avg.gnuplot";
  echo "set output \""$file"_avg.pdf\"" >> $file"_avg.gnuplot";
  echo "plot '$file' using 1:2 with points, '$file""_avg' using 1:2 with lines" >> "$file""_avg.gnuplot";

  gnuplot "$file""_avg.gnuplot";

done;

