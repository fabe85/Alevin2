package vnreal.evaluations.metrics;



import vnreal.demands.AbstractDemand;
import vnreal.evaluations.metrics.utils.ConnectivityCalculator;

import vnreal.evaluations.metrics.utils.MatrixConverter;

import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

/**
 * This class is to calculate the vertex connectivity of the virtual network 
 * considering all hidden hops.
 * @author baskefab
 *
 */
public class VertexConnectivity0 extends AbstractEvaluation {
    
    @Override
    public double calculate() {           
        
        int[][] matrix = MatrixConverter.generateVirtualMatrix(stack);
       
            int numberOfEdges = ConnectivityCalculator.countEdges(matrix);
            int[] nodei = new int[numberOfEdges + 1];
            int[] nodej = new int[numberOfEdges + 1];
            ConnectivityCalculator.transform(matrix, nodei, nodej);
            int numberOfVertices = matrix.length;            
            return ConnectivityCalculator.vertexConnectivity(numberOfVertices, numberOfEdges, nodei, nodej);
       
    }

    
    

   

    public String toString() {
        return ("VertexConnectivity0");
    }
    
    
}