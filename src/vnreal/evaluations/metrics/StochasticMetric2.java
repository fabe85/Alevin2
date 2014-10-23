package vnreal.evaluations.metrics;

import vnreal.demands.AbstractDemand;
import vnreal.evaluations.metrics.utils.ConnectivityCalculator;
import vnreal.evaluations.metrics.utils.MatrixConverter;
import vnreal.evaluations.metrics.utils.PathAlgo;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

/**
 * This class is to calculate the stochastic metric of the virtual network 
 * considering all hidden hops.
 * @author baskefab
 *
 */
public class StochasticMetric2 extends AbstractEvaluation {

    @Override
    public double calculate() {
        
        
        int[][] matrix = MatrixConverter.generateModifiedVirtualNetwork(stack);
        
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
   
        
        
        return  aleph;
        
        /*double counter = 0;
        double result = 0;
        
        int numberOfSubNodes = stack.getSubstrate().getSize();
        int numberOfSubLinks = stack.getSubstrate().getEdgeCount();
        int numberOfSubElem = numberOfSubNodes + numberOfSubLinks;
        
        for (VirtualNetwork vn : stack.getVirtualNetworks()) {
            counter++;
            int size = vn.getSize();
            for (VirtualLink vl : vn.getEdges()) {
                int sizer = 0;
                for (AbstractDemand ad : vl.get()) {
                    sizer += ad.getMappings().size();
                }
                if (sizer > 1) {
                    size++;
                }
            }
            int[][] matrix = new int[size][size];
            int minMeta = vn.getSize();
            for (VirtualLink vl : vn.getEdges()) {
                int equalizer = 0;
                for (VirtualNetwork vnet : stack.getVirtualNetworks()) {
                    if (vnet.getLayer() == vl.getLayer()) {
                        break;
                    } else {
                        for (VirtualLink vlinki : vnet.getEdges()) {
                            equalizer++;
                        }
                        for (VirtualNode vnodi : vnet.getVertices()) {
                            equalizer++;
                        }
                    }
                }
                VirtualNode[] vNodes = new VirtualNode[2];
                int counter2 = 0;
                for (VirtualNode vnn : stack.getVirtualNetworks().get(vl.getLayer() - 1).getIncidentVertices(vl)) {
                    vNodes[counter2] = vnn;
                    counter2++;
                }
                int amountOfMaps = 0;
                for (AbstractDemand ad : vl.get()) {
                    amountOfMaps += ad.getMappings().size();
                }
                if (amountOfMaps > 1) {
                    matrix[(int) (vNodes[0].getId() - equalizer - numberOfSubElem)][minMeta] = 1;
                    matrix[minMeta][(int) (vNodes[0].getId() - equalizer -numberOfSubElem)] = 1;
                    matrix[minMeta][(int) (vNodes[1].getId() - equalizer - numberOfSubElem)] = 1;
                    matrix[(int) (vNodes[1].getId() - equalizer - numberOfSubElem)][minMeta] = 1;
                    minMeta++;
                } else {
                    matrix[(int) (vNodes[0].getId() - equalizer - numberOfSubElem)][(int) (vNodes[1].getId() - equalizer - numberOfSubElem)] = 1;
                    matrix[(int) (vNodes[1].getId() - equalizer - numberOfSubElem)][(int) (vNodes[0].getId() - equalizer - numberOfSubElem)] = 1;
                    
                }
            }
            
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
       
            
            
            result +=  aleph;
        }
        
        return result/counter;
        */
    }

    @Override
    public String toString() {
        return ("StochasticMetric2");
    }

}
