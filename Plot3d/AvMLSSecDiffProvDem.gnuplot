set terminal svg noenhanced dashed size 350,350
set output "Plot3d/AvMLSSecDiffProvDem.svg"
set xlabel 'MLS_Theta' 
set ylabel 'MLS_SecLevels' 
set zlabel 'AvMLSSecDiffProvDem' rotate left
splot 'Plot3d/AvMLSSecDiffProvDem.data' using 1:2:3 with lines title 'min', \
'Plot3d/AvMLSSecDiffProvDem.data' using 1:2:4 with lines title 'max', \
'Plot3d/AvMLSSecDiffProvDem.data' using 1:2:5 with lines title 'average', \
'Plot3d/AvMLSSecDiffProvDem.data' using 1:2:5:6 with errorbars title 'CI95'
