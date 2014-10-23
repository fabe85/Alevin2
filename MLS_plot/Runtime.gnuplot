set terminal svg noenhanced dashed size 350,350
set output "MLS_plot/Runtime.svg"
set xlabel 'MLS_Theta' 
set ylabel 'NumVNodesPerNet' 
set zlabel 'Runtime' rotate left
splot 'MLS_plot/Runtime.data' using 1:2:3 with lines title 'min', \
'MLS_plot/Runtime.data' using 1:2:4 with lines title 'max', \
'MLS_plot/Runtime.data' using 1:2:5 with lines title 'average', \
'MLS_plot/Runtime.data' using 1:2:5:6 with errorbars title 'CI95'
