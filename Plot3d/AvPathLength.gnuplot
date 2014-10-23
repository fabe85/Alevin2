set terminal svg noenhanced dashed size 350,350
set output "Plot3d/AvPathLength.svg"
set xlabel 'MLS_Theta' 
set ylabel 'MLS_SecLevels' rotate left
set zlabel 'AvPathLength' rotate left
splot 'Plot3d/AvPathLength.data' using 1:2:3 title 'min', \
'Plot3d/AvPathLength.data' using 1:2:4 title 'max', \
'Plot3d/AvPathLength.data' using 1:2:5 title 'average', \
'Plot3d/AvPathLength.data' using 1:2:5:6 with errorbars title 'CI95'
