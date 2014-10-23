package vnreal.evaluations.metrics;


import vnreal.evaluations.metrics.utils.ConnectivityCalculator;
import vnreal.evaluations.metrics.utils.MatrixConverter;

/**
 * This class is to calculate the vertex connectivity of the network 
 * represented as an augmented graph matrix.
 * 
 * @author baskefab
 *
 */
public class VertexConnectivity extends AbstractEvaluation {

    @Override
    public double calculate() {
        
        int[][] matrix = MatrixConverter.generateAugmentedGraph(stack);
      
        int numberOfEdges = ConnectivityCalculator.countEdges(matrix);
        int[] nodei = new int[numberOfEdges + 1];
        int[] nodej = new int[numberOfEdges + 1];
        ConnectivityCalculator.transform(matrix, nodei, nodej);        
        int numberOfVertices = matrix.length;
        int result = ConnectivityCalculator.vertexConnectivity(numberOfVertices, numberOfEdges, nodei, nodej);
        return result;
        
      
    }

    @Override
    public String toString() {
        return ("VertexConnectivity");
    }

}
