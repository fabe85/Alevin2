set terminal svg noenhanced dashed size 350,350
set output "Plot2d/AvMLSSecDiffProvDem.svg"
set xlabel 'MLS_Theta' 
set ylabel 'AvMLSSecDiffProvDem' rotate left
plot 'Plot2d/AvMLSSecDiffProvDem.data' using 1:2 title 'min', \
'Plot2d/AvMLSSecDiffProvDem.data' using 1:3 title 'max', \
'Plot2d/AvMLSSecDiffProvDem.data' using 1:4 title 'average', \
'Plot2d/AvMLSSecDiffProvDem.data' using 1:4:5 with errorbars title 'CI95'
