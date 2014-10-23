package vnreal.evaluations.metrics;

import vnreal.evaluations.metrics.utils.ConnectivityCalculator;
import vnreal.evaluations.metrics.utils.MatrixConverter;

/**
 * This class is to calculate the edge connectivity of the network 
 * represented as an augmented graph matrix.
 * 
 * @author baskefab
 *
 */
public class EdgeConnectivity extends AbstractEvaluation {

    @Override
    public double calculate() {
        
        //the augmented graph represented as a matrix
        int[][] matrix = MatrixConverter.generateAugmentedGraph(stack);
        
        
        int numberOfEdges = ConnectivityCalculator.countEdges(matrix);
        int[] nodei = new int[numberOfEdges + 1];
        int[] nodej = new int[numberOfEdges + 1];
        ConnectivityCalculator.transform(matrix, nodei, nodej);        
        int numberOfVertices = matrix.length;
        int result = ConnectivityCalculator.edgeConnectivity(numberOfVertices, numberOfEdges, nodei, nodej);
        return result;
    }

    @Override
    public String toString() {
        return ("EdgeConnectivity");
    }

}
