package vnreal.evaluations.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import vnreal.evaluations.metrics.utils.Combinations;
import vnreal.evaluations.metrics.utils.ConnectivityCalculator;
import vnreal.evaluations.metrics.utils.MatrixConverter;

/**
 * This class is to calculate the resilience factor of the network 
 * represented as an augmented graph matrix.
 * 
 * @author baskefab
 *
 */
public class ResilienceFactor extends AbstractEvaluation {

    @Override
    public double calculate() {
        
        //int[][] matrix = {{0,1,0,1,0},{1,0,0,0,1},{0,0,0,1,0},{1,0,1,0,1},{0,1,0,1,0}};
        //int[][] matrix = {{0,1,0,1,1},{1,0,1,0,0},{0,1,0,1,1},{1,0,1,0,1},{1,0,1,1,0}};
        int[][] matrix = MatrixConverter.generateAugmentedGraph(stack);
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
            double counter = 0.0;
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
                    counter++;
                }
            }
            numerator += counter / numberOfCombinations;
            start++;
        }
        
        return numerator / denominator;
    }

    @Override
    public String toString() {
        return ("ResilientFactor");
    }

}
