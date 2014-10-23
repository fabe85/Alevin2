package vnreal.evaluations.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import vnreal.evaluations.metrics.utils.Combinations;
import vnreal.evaluations.metrics.utils.ConnectivityCalculator;
import vnreal.evaluations.metrics.utils.MatrixConverter;


/**
 * This class is to calculate the resilience factor of the virtual network 
 * considering all hidden hops.
 * @author baskefab
 *
 */
public class ResilienceFactor0 extends AbstractEvaluation {

    @Override
    public double calculate() {
        
        int[][] matrix = MatrixConverter.generateVirtualMatrix(stack);
        
        double denominator = matrix.length - 2;
        double numerator = 0.0;
        int start = 1;
        int end = matrix.length - 2;
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 1; i <= matrix.length; i++) {
                list.add(i);
        }
        while (start <= end) {
            int[] array;
            Set<Set<Integer>> combos = Combinations.combinations(list, start);
            double numberOfCombinations = combos.size();
            double counter2 = 0.0;
            for (Set<Integer> iter : combos) {
                array = Combinations.sort(iter);
                int[][] elim = matrix;
                boolean connected = true;
                for (int i = 0; i < array.length; i++) {
                    elim = Combinations.eliminated(elim, array[i]);
                    if (!ConnectivityCalculator.connected(elim, 0)) {
                        connected = false;
                        break;
                    }
                }
                if (connected) {
                    counter2++;
                }
            }
            numerator += counter2 / numberOfCombinations;
            start++;
        }
                  
        return numerator / denominator;
        
        
        /*
        double counter = 0;
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
            
          //int[][] matrix = {{0,1,0,1,0},{1,0,0,0,1},{0,0,0,1,0},{1,0,1,0,1},{0,1,0,1,0}};
            //int[][] matrix = {{0,1,0,1,1},{1,0,1,0,0},{0,1,0,1,1},{1,0,1,0,1},{1,0,1,1,0}};
            
            double denominator = matrix.length - 2;
            double numerator = 0.0;
            int start = 1;
            int end = matrix.length - 2;
            List<Integer> list = new ArrayList<Integer>();
            for (int i = 1; i <= matrix.length; i++) {
                    list.add(i);
            }
            while (start <= end) {
                int[] array;
                Set<Set<Integer>> combos = Combinations.combinations(list, start);
                double numberOfCombinations = combos.size();
                double counter2 = 0.0;
                for (Set<Integer> iter : combos) {
                    array = Combinations.sort(iter);
                    int[][] elim = matrix;
                    boolean connected = true;
                    for (int i = 0; i < array.length; i++) {
                        elim = Combinations.eliminated(elim, array[i]);
                        if (!ConnectivityCalculator.connected(elim, 0)) {
                            connected = false;
                            break;
                        }
                    }
                    if (connected) {
                        counter2++;
                    }
                }
                numerator += counter2 / numberOfCombinations;
                start++;
            }
                      
            result += numerator / denominator;
        }
        
        return result/counter;
        
        */
        
    }

    @Override
    public String toString() {
        return ("ResilienceFactor0");
    }

}
