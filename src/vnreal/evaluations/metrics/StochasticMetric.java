package vnreal.evaluations.metrics;

import vnreal.evaluations.metrics.utils.ConnectivityCalculator;
import vnreal.evaluations.metrics.utils.MatrixConverter;
import vnreal.evaluations.metrics.utils.PathAlgo;

/**
 * This class is to calculate the stochastic metric of the network 
 * represented as an augmented graph matrix.
 * 
 * @author baskefab
 *
 */
public class StochasticMetric extends AbstractEvaluation {
    

    @Override
    public double calculate() {
        
        int[][] matrix = MatrixConverter.generateAugmentedGraph(stack);
        
        
        PathAlgo path = new PathAlgo();
        boolean[] visited = new boolean[matrix.length];
        path.initialize(matrix, visited);
        int longestPath = path.longestPath(0, visited, matrix);
        for (int i = 1; i < matrix.length; i++) {
            if (path.longestPath(i, visited, matrix) > longestPath) {
                    longestPath = path.longestPath(i, visited, matrix);
            }
            
        }
        double aleph;
        int pathOneNumber = ConnectivityCalculator.countEdges(matrix);
        double probability = 1.0 / (double) matrix.length;
        int divisor = matrix.length * (matrix.length - 1);
        aleph = ((double) (2 * pathOneNumber) / (double) (divisor)) * (1 - (1 - probability)); 
        int i = 2;
        while (i <= longestPath) {
            path.initialize(matrix, visited);
            aleph += ((double) (2 * path.numberOfPaths(i, matrix, visited)) / (double) (divisor)) * (1 - Math.pow((1 - probability), i));
            
            i++;
        }
   
        
        return aleph;
    }

    @Override
    public String toString() {
        return ("StochasticMetric");
    }

}
