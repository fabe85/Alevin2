package vnreal.evaluations.metrics;

import vnreal.evaluations.metrics.utils.ConnectivityCalculator;
import vnreal.evaluations.metrics.utils.MatrixConverter;

public class FabianianCoefficient extends AbstractEvaluation {

    @Override
    public double calculate() {
        
        int[][] matrix = MatrixConverter.generateAugmentedGraph(stack);
        
        int numberOfEdges = ConnectivityCalculator.countEdges(matrix);
        int[] nodei = new int[numberOfEdges + 1];
        int[] nodej = new int[numberOfEdges + 1];
        ConnectivityCalculator.transform(matrix, nodei, nodej);        
        int numberOfVertices = matrix.length;
        int edgeConnectivity = ConnectivityCalculator.edgeConnectivity(numberOfVertices, numberOfEdges, nodei, nodej);     
        int vertexConnectivity = ConnectivityCalculator.vertexConnectivity(numberOfVertices, numberOfEdges, nodei, nodej);
        double nominator = edgeConnectivity + vertexConnectivity;
        
        double delta = 0;
        for (int k = 0; k < matrix[0].length; k++) {
            if (matrix[0][k] == 1) {
                delta++;
            }
        }
        for (int i = 1; i < matrix.length; i++) {
            int counter = 0;
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == 1) {
                    counter++;
                }
            }
            if (counter < delta) {
                delta = counter;
            }
        }
        return nominator / (2 * delta);
    }

    @Override
    public String toString() {
        return ("FabianianCoefficient");
    }

}
