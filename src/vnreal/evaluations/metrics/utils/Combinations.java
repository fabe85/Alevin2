package vnreal.evaluations.metrics.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is to provide the methods required for calculating the resilience
 * factor.
 * 
 * @author baskefab
 *
 */
public class Combinations {

    /**
     * This method is to calculate all possible combinations, i.e. all k-tuples
     * @param groupSize the list of elements to be combined.
     * @param k the number of tuples of combinations.
     * @return all combinations of k-tuples.
     */
    public static Set<Set<Integer>> combinations(List<Integer> groupSize, int k) {

        Set<Set<Integer>> allCombos = new HashSet<Set<Integer>>();
        // base cases for recursion
        if (k == 0) {
            // There is only one combination of size 0, the empty team.
            allCombos.add(new HashSet<Integer>());
            return allCombos;
        }
        if (k > groupSize.size()) {
            // There can be no teams with size larger than the group size,
            // so return allCombos without putting any teams in it.
            return allCombos;
        }

        // Create a copy of the group with one item removed.
        List<Integer> groupWithoutX = new ArrayList<Integer>(groupSize);
        Integer x = groupWithoutX.remove(groupWithoutX.size() - 1);

        Set<Set<Integer>> combosWithoutX = combinations(groupWithoutX, k);
        Set<Set<Integer>> combosWithX = combinations(groupWithoutX, k - 1);
        for (Set<Integer> combo : combosWithX) {
            combo.add(x);
        }
        allCombos.addAll(combosWithoutX);
        allCombos.addAll(combosWithX);
        return allCombos;
    }

    /*
     * public static void main(String[] args) {
     * 
     * int[][] matrix =
     * {{0,1,1,0,0},{1,0,1,0,0},{1,1,0,1,1},{0,0,1,0,1},{0,0,1,1,0}}; int[][]
     * test = eliminated(matrix, 3); for (int i = 0; i < test.length; i++) { for
     * (int j = 0; j < test[i].length; j++) { System.out.print(test[i][j] +
     * " "); if (j == test[i].length - 1) { System.out.println(); } } }
     * System.out.println(); List<Integer> list = new ArrayList<Integer>(); for
     * (int i = 1; i <= matrix.length; i++) { list.add(i); } //List<Integer> a =
     * new ArrayList<Integer>(Arrays.asList(1,2,3,4,5)); Set<Set<Integer>> ab =
     * combinations(list, 3); int[] array; for(Set<Integer> i: ab) { array =
     * sort(i); int[][] elim = matrix; for(int s = 0; s < array.length; s++) {
     * elim = eliminated(elim, array[s]); for (int j = 0; j < elim.length; j++)
     * { for (int k = 0; k < elim[j].length; k++) { System.out.print(elim[j][k]
     * + " "); if (k == elim[j].length - 1) { System.out.println(); } } } }
     * //connected? <<counter>> } }
     */

    /**
     * This method sorts the set of combinations.
     * @param set the set to be sorted.
     * @return the sorted array of the set.
     */
    public static int[] sort(Set<Integer> set) {
        int l = 1;
        int count = 0;
        int[] array = new int[set.size()];
        int j = 0;
        for (Integer k : set) {
            array[j] = k;
            j++;
        }
        insertionSort(array);

        while (count < array.length) {
            array[count] -= l;
            l++;
            count++;
        }
        return array;
    }

    private static void insertionSort(int[] array) {
        int w = 1;
        while (w < array.length) {
            insert(array, w);
            w++;
        }
    }

    private static void insert(int[] array, int w) {
        for (int i = w; i >= 1; i--) {
            if (array[i - 1] <= array[i]) {
                break;
            }
            int temp = array[i - 1];
            array[i - 1] = array[i];
            array[i] = temp;
        }
    }

    /**
     * This method creates new graphs represented as matrices where the
     * corresponding node is eliminated.
     * 
     * @param matrix
     *            The matrix representing the graph.
     * @param node
     *            The node of the graph which is to be eliminated.
     * @return a new matrix without the node @node.
     */
    public static int[][] eliminated(int[][] matrix, int node) {

        int size = matrix.length - 1;
        int[][] elim = new int[size][size];
        int i = 0;
        int k = 0;
        while (i < matrix.length && k < elim.length) {
            int j = 0;
            int l = 0;
            while (j < matrix.length && l < elim.length) {

                if (i == node) {
                    j = matrix[i].length;
                    i++;
                } else if (j == node) {
                    j++;
                } else {
                    elim[k][l] = matrix[i][j];
                    if (l == elim[k].length - 1) {
                        i++;
                        k++;
                    }
                    l++;
                    j++;
                }
            }
        }
        return elim;
    }

}
