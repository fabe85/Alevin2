set terminal svg noenhanced dashed size 350,350
set output "Plot2d/AvPathLength.svg"
set xlabel 'MLS_Theta' 
set ylabel 'AvPathLength' rotate left
plot 'Plot2d/AvPathLength.data' using 1:2 title 'min', \
'Plot2d/AvPathLength.data' using 1:3 title 'max', \
'Plot2d/AvPathLength.data' using 1:4 title 'average', \
'Plot2d/AvPathLength.data' using 1:4:5 with errorbars title 'CI95'
