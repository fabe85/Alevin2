package vnreal.evaluations.metrics;

import java.util.ArrayList;
import java.util.Collection;

import vnreal.demands.AbstractDemand;
import vnreal.evaluations.metrics.utils.ConnectivityCalculator;
import vnreal.evaluations.metrics.utils.FordFulkerson;
import vnreal.evaluations.metrics.utils.LargestEigenvalueFinder;
import vnreal.evaluations.metrics.utils.Matrix;
import vnreal.evaluations.metrics.utils.MatrixConverter;
import vnreal.mapping.Mapping;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

/**
 * This class is to calculate the vertex connectivity of the virtual network 
 * considering all hidden hops.
 * @author baskefab
 *
 */
public class VertexConnectivity2 extends AbstractEvaluation {
    
    @Override
    public double calculate() {           
        
        int[][] matrix = MatrixConverter.generateModifiedVirtualNetwork(stack);
       
            int numberOfEdges = ConnectivityCalculator.countEdges(matrix);
            int[] nodei = new int[numberOfEdges + 1];
            int[] nodej = new int[numberOfEdges + 1];
            ConnectivityCalculator.transform(matrix, nodei, nodej);
            int numberOfVertices = matrix.length;            
            return ConnectivityCalculator.vertexConnectivity(numberOfVertices, numberOfEdges, nodei, nodej);
       
    }

    /**
     * Test method to calculate the average vertex connectivity of multiple 
     * virtual networks
     * @return the average vertex connectivity of multiple virtual networks
     */
    public double calculate2() {           
        
        
        
        int counter = 0;
        int result = 0;
        
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
            int numberOfEdges = ConnectivityCalculator.countEdges(matrix);
            int[] nodei = new int[numberOfEdges + 1];
            int[] nodej = new int[numberOfEdges + 1];
            ConnectivityCalculator.transform(matrix, nodei, nodej);
            int numberOfVertices = matrix.length;            
            result += ConnectivityCalculator.vertexConnectivity(numberOfVertices, numberOfEdges, nodei, nodej);
        }
        
        return result/counter;
    }

   

    public String toString() {
        return ("VertexConnectivity2");
    }
    
    
}