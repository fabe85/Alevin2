set terminal svg noenhanced dashed size 350,350
set output "TestReport/TestGraph.svg"
set xlabel 'X-Label' 
set ylabel 'Y-Label' rotate left
set border 3
set xtics nomirror
set ytics nomirror 1
set xrange [  :  ]
set yrange [  :  ]
plot 'TestReport/TestTemplate/FitTest_Count.data'  using 1:2:3 with points ps variable lc 1 pt 7 title 'FitTest_Count' , \
'TestReport/TestTemplate/UniqueTest_Count.data'  using 1:2:3 with points ps variable lc 2 pt 5 title 'UniqueTest_Count' 
