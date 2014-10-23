package vnreal.evaluations.metrics.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PathAlgo {

    public static void main(String[] args) {
        
        //int adjMatrix[][] = {{0,1,1,1,1},{1,0,0,0,0},{1,0,0,0,0},{1,0,0,0,0},{1,0,0,0,0}};
        int[][] adjMatrix = {{0,1,0,1,0},{1,0,0,0,1},{0,0,0,1,0},{1,0,1,0,1},{0,1,0,1,0}};

        
        PathAlgo lpa = new PathAlgo();
        boolean visited[] = new boolean[adjMatrix.length];
        lpa.initialize(adjMatrix, visited);
        int max = lpa.longestPath(0, visited, adjMatrix);
        for (int i = 1; i < adjMatrix.length; i++) {
                if (lpa.longestPath(i, visited, adjMatrix) > max) {
                        max = lpa.longestPath(i, visited, adjMatrix);
                }
        }
        
        System.out.println("Longest Path Length = "+max);
        
        PathAlgo test = new PathAlgo();
        int pathLength = 3;
        boolean visited2[] = new boolean[adjMatrix.length];
        int amount = test.numberOfPaths(pathLength, adjMatrix, visited2);
        System.out.println("Number Of Paths = " + amount);
        
    }

    public void initialize(int adjMatrix[][], boolean visited[]) {
        for (int u = 0; u < adjMatrix.length; u++) {
            visited[u] = false;
        }
    }

    public int longestPath(int vertex, boolean visited[], int adjMatrix[][]) {
        int dist, max = 0;
        visited[vertex] = true;

        for (int u = 0; u < adjMatrix[vertex].length; u++) {
            if (adjMatrix[vertex][u] == 1) {
                if (!visited[u]) {
                    dist = adjMatrix[vertex][u] + longestPath(u, visited, adjMatrix);
                    if (dist > max) {
                        max = dist;
                    }
                }
            }
        }
        visited[vertex] = false;
        return max;
    }
    
    /*
    int helper(int vertex, Collection<List<Integer>> visited, int[][] matrix, int pathLength, int amount, int origPathLength, Collection<List<List<Integer>>> sum) {
        int dist = 0; 
        
        boolean forward = true;
        for (int u = 0; u < matrix[vertex].length; u++) {
            for (List<Integer> iter : visited) {
                int alarm = 0;
                for (Integer numb : iter) {
                    if (numb == vertex || numb == u) {
                        alarm++;
                    }
                }
                if (alarm == 2) {
                    forward = false;
                }
            }
            
                if (forward && matrix[vertex][u] == 1) {
                    List<Integer> elem = new ArrayList<Integer>(); 
                    elem.add(vertex);
                    elem.add(u);
                    
                    visited.add(elem);
                        if (matrix[vertex][u] == pathLength) {
                                //visited[u] = true; 
                            boolean next = true;
                               
                               
                                
                                for (List<Integer> trans : visited) {
                                    Collections.sort(trans);
                                }
                                for (List<List<Integer>> list1 : sum) {
                                for (List<Integer> iter : list1) {
                                    Collections.sort(iter);                                 
                                                                           
                                    }
                                    if (list1.containsAll(visited) && visited.containsAll(list1)) {
                                        next = false;
                                        break;
                                    } 
                                    }
                                if (next) {
                                    amount++;
                                    List<List<Integer>> container = new ArrayList<List<Integer>>();
                                    for (List<Integer> itera : visited) {
                                        List<Integer> pairs = new ArrayList<Integer>();
                                        for (Integer number : itera) {
                                            pairs.add(number);
                                        }
                                        container.add(pairs);
                                    }
                                    
                                    sum.add(container);
                                    
                                    
                                    
                                }
                                
                        } else {
                            
                                amount = helper(u, visited, matrix, pathLength - 1, amount, origPathLength, sum);
                        }
                                                 
                                
                        
                } else {
                    forward = true;
                }
        }
        return amount;
    }
    
    int numberOfPaths(int pathLength, int[][] matrix) {
        Collection<List<List<Integer>>> list = new ArrayList<List<List<Integer>>>();
        int dist = 0;
        int amount = 0;
        for (int i = 0; i < matrix.length; i++) {
                int vertex = i;                
                for (int u = 0; u < matrix[vertex].length; u++) {
                        Collection<List<Integer>> visited = new ArrayList<List<Integer>>();
                        List<Integer> iter = new LinkedList<Integer>();
                        iter.add(i);
                        iter.add(u);
                        visited.add(iter);
                        
                        
                            if (matrix[vertex][u] == 1) {
                                
                                amount = helper(u, visited, matrix, pathLength - 1, amount, pathLength, list);
                                }                            
                                
                        
                }
        }
        return amount;
    }*/
    
    
    private int helper(int vertex, boolean visited[], int[][] matrix, int pathLength, int amount, Collection<List<Integer>> list, int origPathLength) {
        int dist = 0; 
        visited[vertex] = true;
        
        for (int u = 0; u < matrix[vertex].length; u++) {
                if (!visited[u] && matrix[vertex][u] == 1) {
                        if (matrix[vertex][u] == pathLength) {
                                //visited[u] = true; 
                            boolean next = true;
                                dist = pathLength;
                                int counter = origPathLength + 1;
                               
                                int size = counter;
                                for (List<Integer> iter : list) {
                                    for (Integer number : iter) {
                                        for (int k = 0; k < visited.length; k++) {
                                            if (visited[k]) {
                                                if (k == number) {
                                                    counter--;
                                                }
                                            }
                                        }
                                        if (number == u) {
                                            counter--;
                                        }                                        
                                    }
                                    if (counter == 0 && iter.size() == size) {
                                        next = false;
                                        break;
                                    } else {
                                        counter = origPathLength + 1;
                                    }
                                }
                                if (next) {
                                    amount++;
                                    List<Integer> elem = new LinkedList<Integer>();                                
                                    for (int i = 0; i < visited.length; i++) {
                                        if (visited[i]) {
                                            elem.add(i);
                                        }
                                        if (i == u) {
                                            elem.add(i);
                                        }
                                    }
                                    
                                    list.add(elem);
                                }
                                
                                
                        } else {
                                amount = helper(u, visited, matrix, pathLength - 1, amount, list, origPathLength);
                        }
                }
        }
        return amount;
    }
    
    public int numberOfPaths(int pathLength, int[][] matrix, boolean[] visited) {
        Collection<List<Integer>> list = new ArrayList<List<Integer>>();
        int dist = 0;
        int amount = 0;
        for (int i = 0; i < matrix.length; i++) {
                int vertex = i;                
                for (int u = 0; u < matrix[vertex].length; u++) {
                        visited = new boolean[visited.length];
                        visited[i] = true;
                        if (!visited[u]) {
                            if (matrix[vertex][u] == 1) {
                                amount = helper(u, visited, matrix, pathLength - 1, amount, list, pathLength);
                                }                            
                                
                        }
                }
        }
        return amount;
    }
}