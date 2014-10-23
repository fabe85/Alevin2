package vnreal.evaluations.metrics.utils;

import java.util.Stack;


public class ConnectivityCalculator {
        
        /**
         * This method counts the edges of undirected graphs.
         * @param matrix the graph represented as a matrix.
         * @return the number of edges.
         */
        public static int countEdges(int matrix[][]) {
                int numberOfEdges = 0;
                for (int i = 0; i < matrix[0].length; i++) {
                        for (int j = i + 1; j < matrix[0].length; j++) {
                                if (matrix[i][j] == 1) {
                                        numberOfEdges++;
                                }
                        }
                }
                return numberOfEdges;
        }
        
        /**
         * This method transforms a graph represented as a matrix into two arrays whose
         * elements are the corresponding end nodes of each edge of the graph.
         * @param matrix the graph represented as a matrix.
         * @param nodei one array whose length is the number of edges in the graph plus 1 as 
         *                      the first element is always 0 (because of the computation of the maxFlow).
         *                      
         * @param nodej the other array which is to be filled with the end nodes of each edge 
         *                      of the graph with the same size like nodei.
         */
        public static void transform(int matrix[][], int nodei[], int nodej[]) {
                int count = 1;
                nodei[0] = 0;
                nodej[0] = 0;
                for (int i = 0; i < matrix[0].length; i++) {
                        for (int j = i + 1; j < matrix[0]. length; j++) {
                                if (matrix[i][j] == 1) {
                                        nodei[count] = i + 1;
                                        nodej[count] = j + 1;
                                        count++;
                                }
                        }
                }
        }

        /**
         * This methods checks if there is an edge from node i to node j.
         * @param nodei an array with end nodes of each edge.
         * @param nodej the other array with the corresponding end nodes of each edge.
         * @param i the node to be checked if there is an edge starting or ending in it.
         * @param j the other node to be checked if there is an edge starting or ending in it.
         * @return <true> if and only if there is an edge from node i to j or vice versa.
         */
        public static boolean isEdge(int nodei[], int nodej[], int i, int j) {
                boolean isEdge = false;
                for (int a = 1; a < nodei.length; a++) {
                        if ((nodei[a] == i && nodej[a] == j) || (nodei[a] == j && nodej[a] == i)) {
                                isEdge = true;
                        }
                }
                return isEdge;
        }

        /**
         * This method calculates the vertex connectivity of a graph.
         * @param n the number of nodes of the undirected graph, labeled from 1 to n.
         * @param m the number of edges of the graph.
         * @param nodei an array with the end nodes of each edge.
         * @param nodej the other array with the corresponding end nodes of each edge.
         * @return the vertex connectivity of the graph.
         *
        public static int vertexConnectivity(int n, int m, int nodei[], int nodej[]) {
                int capac[] = new int[4*m + 1];
                int arcflow[] = new int[4*m + 1];
                int nodeflow[] = new int[4*m + 1];
                int edgei[] = new int[4*m + 1];
                int edgej[] = new int[4*m + 1];
                int minimumcut[] = new int[n + 1];
                int m2 = m+m;
                int kappa_min = n-1;
                int i = 1; 
                while (i <= kappa_min) {
                        for (int j = i + 1; j <= n; j++) {
                                if (i > kappa_min) {
                                        break;
                                } else if (!isEdge(nodei, nodej, i,j)) {
                                        for (int a = 1; a <= 4*m; a++) {
                                                edgei[a] = 0;
                                                edgej[a] = 0;
                                                capac[a] = 0;
                                        }
                                        int e = 0;
                                        for (int a = 1; a <= m ; a++) {
                                                e++;
                                                edgei[e] = nodei[a];
                                                edgej[e] = nodej[a];
                                                capac[e] = 1;
                                                e++;
                                                edgei[e] = nodej[a];
                                                edgej[e] = nodei[a];
                                                capac[e] = 1;
                                        }
                                        maximumNetworkFlow(n, m2, edgei, edgej, capac, i, j, minimumcut, arcflow, nodeflow );
                                        int local = nodeflow[i];
                                        kappa_min = Math.min(kappa_min, local);
                                }
                        }
                        i++;
                }
                return kappa_min;
        }
    
        /**
         * This method calculates the edge connectivity of an undirected graph.
         * @param numberOfNodes The number of nodes of the undirected graph, labeled from 1 to 
         *                      numberOfNodes.
         * @param numberOfEdges The number of edges of the graph.
         * @param nodei An array with the end nodes of each edge of the graph.
         * @param nodej The other array with the corresponding end nodes of the graph.
         * @return the edge connectivity of the graph.
         */
    public static int edgeConnectivity(int numberOfNodes, int numberOfEdges, int[] nodei, int[] nodej)
    {
      int[] minimumcut = new int[numberOfNodes + 1];
      int[] edgei = new int[4 * numberOfEdges + 1];
      int[] edgej = new int[4 * numberOfEdges + 1];
      int[] capac = new int[4 * numberOfEdges + 1];
      int[] arcflow = new int[4 * numberOfEdges + 1];
      int[] nodeflow = new int[4 * numberOfEdges + 1];
      
      int k = numberOfNodes;
      int source = 1;
      int m = numberOfEdges + numberOfEdges;
      for (int sink = 2; sink <= numberOfNodes; sink++)
      {
        for (int i = 1; i <= 4 * numberOfEdges; i++)
        {
          edgei[i] = 0;
          edgej[i] = 0;
          capac[i] = 0;
        }
        int j = 0;
        for (int i = 1; i <= numberOfEdges; i++)
        {
          j++;
          edgei[j] = nodei[i];
          edgej[j] = nodej[i];
          capac[j] = 1;
          j++;
          edgei[j] = nodej[i];
          edgej[j] = nodei[i];
          capac[j] = 1;
        }
        maximumNetworkFlow(numberOfNodes, m, edgei, edgej, capac, source, sink, minimumcut, arcflow, nodeflow);
        if (nodeflow[source] < k) {
          k = nodeflow[source];
        }
      }
      return k;
    }
    
    /**
     * This method calculates the vertex connectivity of a graph.
     * @param n the number of nodes of the undirected graph, labeled from 1 to n.
     * @param m the number of edges of the graph.
     * @param nodei an array with the end nodes of each edge.
     * @param nodej the other array with the corresponding end nodes of each edge.
     * @return the vertex connectivity of the graph.
     */
    public static int vertexConnectivity2(int numberOfNodes, int numberOfEdges, int[] nodei, int[] nodej) {
        int size = 2*numberOfNodes - 2;
        int[] minimumcut = new int[size + 1];
        int[] edgei = new int[4*numberOfEdges + 1];
        int[] edgej = new int[4*numberOfEdges + 1];
        int[] capac = new int[4*numberOfEdges + 1 + 2*(numberOfNodes - 2)];
        int[] arcflow = new int[4*numberOfEdges + 1 + 2*(numberOfNodes - 2)];
        int[] nodeflow = new int[4*numberOfEdges + 1 + 2*(numberOfNodes - 2)];
        int[] pseudoi = new int[4*numberOfEdges + 1 + 2*(numberOfNodes - 2)];
        int[] pseudoj = new int[4*numberOfEdges + 1 + 2*(numberOfNodes - 2)];
        
        
        int k = 2*numberOfNodes - 2;
        int source = 1;
        int m = 2 * numberOfEdges + numberOfNodes - 2;
        for (int sink = 2; sink <= numberOfNodes; sink++) {
                for (int i = 1; i <= 4*numberOfEdges; i++) {
                        edgei[i] = 0;
                        edgej[i] = 0;
                        capac[i] = 0;
                }
                int j = 0;
                for (int i = 1; i <= numberOfEdges; i++) {
                        j++;
                        edgei[j] = nodei[i];
                        edgej[j] = nodej[i];
                        capac[j] = 1;
                        j++;
                        edgei[j] = nodej[i];
                        edgej[j] = nodei[i];
                        capac[j] = 1;
                }
        }
        for (int i = 1; i <= 2*numberOfEdges; i++) {
                if (edgei[i] < numberOfNodes) {
                        pseudoi[i] = edgei[i] + edgei[i] - 1;
                } else {
                        pseudoi[i] = edgei[i] + edgei[i] - 2;
                }
                if (edgej[i] > 1) {
                        pseudoj[i] = edgej[i] + edgej[i] - 2;
                } else {
                        pseudoj[i] = edgej[i] + edgej[i] - 1;
                }
                                
        }
        for (int sink = 2; sink <= numberOfNodes; sink++) {
                
                int max = sink + sink - 2;
                int p = source + 1;
                int q = max + 1;
                for (int i = 2* numberOfEdges + 1; i < 2*numberOfEdges + 1 + numberOfNodes - 2; i++) {
                        if (p < max) {
                                pseudoi[i] = p;
                                p++;
                                pseudoj[i] = p; 
                                p++;
                        } else {
                                pseudoi[i] = q; 
                                q++;
                                pseudoj[i] = q;
                                q++;
                        }
                        capac[i] = 1;
                }
                //maximumNetworkFlow(size, m, pseudoi, pseudoj, capac, source, sink, minimumcut, arcflow, nodeflow);
                int t = vertexConnectivity(size, m, pseudoi, pseudoj);
                
                if (t < k) {
                        k = t;
                }
        }
        return k;
        
    }
    
    /**
     * This method calculates the vertex connectivity of a graph.
     * @param n the number of nodes of the undirected graph, labeled from 1 to n.
     * @param m the number of edges of the graph.
     * @param nodei an array with the end nodes of each edge.
     * @param nodej the other array with the corresponding end nodes of each edge.
     * @return the vertex connectivity of the graph.
     */
    public static int vertexConnectivity(int n, int m, int nodei[], int nodej[]) {
                int capac[] = new int[4*m + 1];
                int arcflow[] = new int[4*m + 1];
                int nodeflow[] = new int[4*m + 1];
                int edgei[] = new int[4*m + 1];
                int edgej[] = new int[4*m + 1];
                int minimumcut[] = new int[n + 1];
                int m2 = m+m;
                int kappa_min = n-1;
                int i = 1; 
                while (i <= kappa_min) {
                        for (int j = i + 1; j <= n; j++) {
                                if (i > kappa_min) {
                                        break;
                                } else if (!isEdge(nodei, nodej, i,j)) {
                                        for (int a = 1; a <= 4*m; a++) {
                                                edgei[a] = 0;
                                                edgej[a] = 0;
                                                capac[a] = 0;
                                        }
                                        int e = 0;
                                        for (int a = 1; a <= m ; a++) {
                                                e++;
                                                edgei[e] = nodei[a];
                                                edgej[e] = nodej[a];
                                                capac[e] = 1;
                                                e++;
                                                edgei[e] = nodej[a];
                                                edgej[e] = nodei[a];
                                                capac[e] = 1;
                                        }
                                        maximumNetworkFlow(n, m2, edgei, edgej, capac, i, j, minimumcut, arcflow, nodeflow );
                                        int local = nodeflow[i];
                                        kappa_min = Math.min(kappa_min, local);
                                }
                        }
                        i++;
                }
                return kappa_min;
        }
    
    /*
    public static void main(String[] args) {
        int[] array1 = { 0,1,1,1,2,3,4,2,3 };
        int[] array2 = { 0,2,3,4,5,5,5,3,4};
        int n = 5;
        int m = 8;
        int a = vertexConnectivity2(n, m, array1, array2);
        System.out.println(a);
    }*/
    
    
    /*
    public static void main(String[] paramArrayOfString)
    {
      int j = 5;
      int k = 4;
      int[] arrayOfInt1 = { 0,1,3,4,3 };
      int[] arrayOfInt2 = { 0,3,5,3,2};
      int[] nodeii = {0,8,3,5,1,3,7,2,6,7,4};
      int[] nodejj = {0,4,7,2,6,8,4,1,5,8,3};
      
      int[][] matrix = {{0,1,1,0,0,0,0},{1,0,0,1,1,0,0},{1,0,0,1,0,0,0},{0,1,1,0,1,0,1},
                  {0,1,0,1,0,1,0}, {0,0,0,0,1,0,1},{0,0,0,1,0,1,0}};
      
      int[][] matrix2 = {{0,0,0,1,0,0,0,0},{0,0,1,0,0,0,0,0},{0,0,0,1,0,0,0,0},{0,0,0,0,1,0,0,0},
                  {1,1,0,0,0,1,0,1},{0,0,0,0,0,0,1,0},{0,0,0,1,0,0,0,0},{0,0,0,1,0,0,0,0}};
      
      int[][] matrix3 = {{0,1,0,0,0,1,0,0},{1,0,0,0,1,0,0,0},{0,0,0,1,0,0,1,1},{0,0,1,0,0,0,1,1},
                  {0,1,0,0,0,1,0,0},{1,0,0,0,1,0,0,0},{0,0,1,1,0,0,0,1},{0,0,1,1,0,0,1,0}};
      
      int numberOfEdges = countEdges(matrix);
      int numberOfVertices = matrix[0].length;
      int[] nodei = new int[numberOfEdges + 1];
      int[] nodej = new int[numberOfEdges + 1];
      transform(matrix, nodei, nodej);
      System.out.print("The nodei = ");
      for (int i = 0; i < nodei.length; i++) {
          System.out.print(nodei[i]+ " ");
      }
      System.out.println();
      System.out.print("The nodej = ");
      for (int i = 0; i < nodej.length; i++) {
          System.out.print(nodej[i] + " ");
      }
      System.out.println();
      
      int i = edgeConnectivity(j, k, arrayOfInt1, arrayOfInt2);
      System.out.println("The edge connectivity of the graph = " + i);
      int a = vertexConnectivity(j, k, arrayOfInt1, arrayOfInt2);
      System.out.println("The vertex connectivity of the graph = " + a);
      int z = edgeConnectivity(numberOfVertices, numberOfEdges, nodei, nodej);
      System.out.println("The edge connectivity of matrix = " + z);
      int u = vertexConnectivity(numberOfVertices, numberOfEdges, nodei, nodej);
      System.out.println("The vertex connectivity of the matrix = " + u);
      //boolean connected = connected(j, k, arrayOfInt1, arrayOfInt2);
      //System.out.println("connected: " + connected);
      int[][] matrixTest = FordFulkerson.retransform(nodei, nodej, 7);
      for(int s = 0; s < matrixTest.length; s++) {
          for (int t = 0; t < matrixTest[i].length; t++) {
              System.out.print(matrixTest[s][t] + "");
              if (t == matrixTest[s].length - 1) {
                  System.out.println();
              }
          }
      }
      int ab = FordFulkerson.edgeConnectivity(matrixTest, matrixTest.length - 1);
      System.out.println("TTT The edge connectivity of the graph = " + ab);
      System.out.println();
      
      int[][] nmatrix = FordFulkerson.retransform(arrayOfInt1, arrayOfInt2, 5);
      for(int s = 0; s < nmatrix.length; s++) {
          for (int t = 0; t < nmatrix[i].length; t++) {
              System.out.print(nmatrix[s][t] + "");
              if (t == nmatrix[s].length - 1) {
                  System.out.println();
              }
          }
      }
      
      dfs(matrix, 1);
    }*/
    
    
    /**
     * This method checks if the graph represented as matrix is connected, i.e. if there
     * is a path from each vertex to each other vertex in the network.
     * @param matrix The matrix representing the graph.
     * @param source The source node of the graph.
     */
    public static boolean connected(int matrix[][], int source) {
        
        Stack<Integer> stack = new Stack<Integer>();

        int number_of_nodes = matrix[source].length - 1;        

        int visited[] = new int[number_of_nodes + 1];           

        int element = source;           

        int i = source; 

        visited[source] = 1;            

        stack.push(source);

        while (!stack.isEmpty()) {

            element = stack.peek();

            i = element;        

            
            while (i <= number_of_nodes) {

                if (matrix[element][i] == 1 && visited[i] == 0) {

                    stack.push(i);

                    visited[i] = 1;

                    element = i;

                    i = 1;

                    continue;

                }

                i++;

            }

            
            stack.pop();        

        }

        boolean connected = false; 

        for (int vertex = 1; vertex <= number_of_nodes; vertex++) {

            if (visited[vertex] == 1) {

                connected = true;

            } else {

                connected = false;

                break;

            }

        }

 

        if (connected) {

            //System.out.println("The graph is connected");
            return true;

        } else {

            //System.out.println("The graph is disconnected");
            return false;

        }

    }
    

    /**
     * This method checks if a graph is connected, i.e. if there is a path from each
     * vertex to another vertex.
     * @param n The number of nodes of the graph.
     * @param m The number of edges of the graph.
     * @param nodei An array containing all end nodes of the graph.
     * @param nodej The other array containing all corresponding end nodes of the graph.
     * @return <true> if and only if the graph is connected.
     */
    public static boolean connected(int n, int m, int nodei[], int nodej[]) {
        int i;
        int j;
        int k;
        int r;
        int connect;
        int neighbor[] = new int[m + m + 1];
        int degree[] = new int[n + 1];
        int index[] = new int[n + 2];
        int aux1[] = new int[n + 1];
        int aux2[] = new int[n + 1];
        
        for (i = 1; i <= n; i++) {
                degree[i] = 0;
        }
        for (j = 1; j <= m; j++) {
                degree[nodei[j]]++;
                degree[nodej[j]]++;
        }
        index[1] = 1;
        for (i = 1; i <= n; i++) {
                index[i + 1] = index[i] + degree[i];
                degree[i] = 0;
        }
        for (j = 1; j <= m; j++) {
                neighbor[index[nodei[j]]] = degree[nodei[j]] + nodej[j];
                degree[nodei[j]]++;
                neighbor[index[nodej[j]]] = degree[nodej[j]] + nodei[j];
                degree[nodej[j]]++;
        }
        for (i = 2; i <= n; i++) {
                aux1[i] = 1;
        }
        aux1[1] = 0;
        connect = 1;
        aux2[1] = 1;
        k = 1;
        while(true) {
                i = aux2[k];
                k--;
                for (j = index[i]; j <= index[i + 1] - 1; j++) {
                        r = neighbor[j];
                        if (aux1[r] != 0) {
                                connect++;
                                if (connect == n) {
                                        connect /= n;
                                        if (connect == 1) {
                                                return true;
                                        } else {
                                                return false;
                                        }
                                }
                                aux1[r] = 0;
                                k++;
                                aux2[k] = r;
                        }
                }
                if (k == 0) {
                        connect /= n;
                        if (connect == 1) {
                                return true;
                        } else {
                                return false;
                        }
                }
        }
    }
    
    /**
     * This method calculates the maximum flow.
     * @param n The number of nodes of the network, labeled from 1 to n.
     * @param m The number of edges in the network.
     * @param nodei An array with the end nodes of each edge of the network.
     * @param nodej The other array with the corresponding end nodes of each edge of the network.
     * @param capacity An array with the capacity of each edge of the network.
     * @param source The source node of the graph.
     * @param sink The sink node of the graph.
     * @param minimumcut An array filled with the information if the corresponding node
     *                  is situated in the minimum cut set (then the value is 1, otherwise 0).
     * @param arcflow An array telling the amount of flow through the corresponding edge.
     * @param nodeflow An array telling the amount of flow through the corresponding node.
     */
    public static void maximumNetworkFlow(int n, int m, int nodei[], int nodej[], int capacity[], int source, int sink, int minimumcut[], int arcflow[], int nodeflow[]) {
        
        int i,j,curflow, flag,medge,nodew,out;
        int in = 0, iout = 0, parm = 0, m1 = 0, icont= 0, jcont = 0;
        int last = 0, nodep = 0, nodeq = 0, nodeu = 0, nodev = 0, nodex = 0, nodey = 0;
        int firstarc[] = new int[n+1];
        int imap[] = new int[n+1];
        int jmap[] = new int[n+1];
        boolean finish,controla, controlb, controlc, controlg;
        boolean controld = false, controle= false, controlf = false;
        
        j = m;
        for (i = 1; i <= n; i++) {
            firstarc[i] = 0;
        }
        
        curflow = 0;
        for (i = 1; i <= m; i++) {
            arcflow[i] = 0;
            j = nodei[i];
            if (j == source) curflow += capacity[i];
            firstarc[j]++;
        }
        nodeflow[source] = curflow;
        nodew = 1;
        for (i = 1; i <=n; i++) {
            j = firstarc[i];
            firstarc[i] = nodew;
            imap[i] = nodew;
            nodew += j;
        }
        finish = false;
        controla = true;
        entry1:
            while (true) {
                flag = 0;
                controlb = false;
                entry2: while (true) {
                    if (!controlb) {
                        if ((flag < 0) && controla) {
                            if (flag != -1) {
                                if (nodew < 0) nodep++;
                                nodeq = jcont;
                                jcont = nodep;
                                flag = -1;
                            } else {
                                if (nodew <= 0) {
                                    if (icont > 1) {
                                        icont--;
                                        jcont = icont;
                                        controla = false;
                                        continue entry2;
                                    }
                                    if (m1 == 1)
                                        flag = 0;
                                    else {
                                        nodep = m1;
                                        m1--;
                                        nodeq = 1;
                                        flag = 1;
                                    }
                                } else flag = 2;
                            }
                        } else {
                            if (controla)
                                if (flag > 0) {
                                    if (flag <= 1) jcont = icont;
                                    controla = false;
                                }
                            if (controla) {
                                m1 = m;
                                icont = 1 + m/2;
                                icont--;
                                jcont = icont;
                            }
                            controla = true;
                            nodep = jcont + jcont;
                            if (nodep < m1) {
                                nodeq = nodep + 1;
                                flag = -2;
                            } else {
                                if (nodep == m1) {
                                    nodeq = jcont;
                                    jcont = nodep;
                                    flag = -1;
                                } else {
                                    if (icont > 1) {
                                        icont--;
                                        jcont = icont;
                                        controla = false;
                                        continue entry2;
                                    }
                                    if (m1 == 1)
                                        flag = 0;
                                    else {
                                        nodep = m1; 
                                        m1--;
                                        nodeq = 1;
                                        flag = 1;
                                    }
                                }
                            }
                        }
                    }
                    controlg = false;
                    controlc = false;
                    if ((flag < 0) && !controlb) {
                        nodew = nodei[nodep] - nodei[nodeq];
                        if (nodew == 0) nodew = nodej[nodep] - nodej[nodeq];
                        continue entry2;
                    } else {
                        if ((flag > 0) || controlb) {
                            controlb = false;
                            nodew = nodei[nodep];
                            nodei[nodep] = nodei[nodeq];
                            nodei[nodeq] = nodew;
                            curflow = capacity[nodep];
                            capacity[nodep] = capacity[nodeq];
                            capacity[nodeq] = curflow;
                            nodew = nodej[nodep];
                            nodej[nodep] = nodej[nodeq];
                            nodej[nodeq] = nodew;
                            curflow = arcflow[nodep];
                            arcflow[nodep] = arcflow[nodeq];
                            arcflow[nodeq] = curflow;
                            if (flag > 0) continue entry2;
                            if (flag == 0) {
                                controlc = true;
                            } else {
                                jmap[nodev] = nodeq;
                                controlg = true;
                            }
                            
                        } else if (finish) {
                            j = 0;
                            for (i = 1; i <= m; i++) 
                                if (arcflow[i] > 0) {
                                    j++;
                                    nodei[j] = nodei[i];
                                    nodej[j] = nodej[i];
                                    arcflow[j] = arcflow[i];
                                }
                            arcflow[0] = j;
                            return;
                        }
                    }
                    if (!controlg && !controlc) {
                        for (i = 1; i <= m; i++) {
                            nodev = nodej[i];
                            nodei[i] = imap[nodev];
                            imap[nodev]++;
                        }
                    }
                    
                    entry3:
                        while (true) {
                            if (!controlg) {
                                if (!controlc) {
                                    flag = 0;
                                    for (i = 1; i <=n; i++) {
                                        if (i != source) nodeflow[i] = 0;
                                        jmap[i] = m + 1;
                                        if (i < n) jmap[i] = firstarc[i + 1];
                                        minimumcut[i] = 0;
                                    }
                                    in = 0;
                                    iout = 1;
                                    imap[1] = source;
                                    minimumcut[source] = -1;
                                    while (true) {
                                        in++;
                                        if (in > iout) break;
                                        nodeu = imap[in];
                                        medge = jmap[nodeu] - 1;
                                        last = firstarc[nodeu] - 1;
                                        while (true) {
                                            last++;
                                            if (last > medge) break;
                                            nodev = nodej[last];
                                            curflow = capacity[last] - arcflow[last];
                                            if ((minimumcut[nodev] != 0) || (curflow == 0)) continue;
                                            if (nodev != sink) {
                                                iout++;
                                                imap[iout] = nodev;
                                            }
                                            minimumcut[nodev] = -1;
                                        }
                                    }
                                    if (minimumcut[sink] == 0) {
                                        for (i = 1; i <= n; i++) 
                                            minimumcut[i] = -minimumcut[i];
                                        for (i = 1; i <= m; i++) {
                                            nodeu = nodej[nodei[i]];
                                            if (arcflow[i] < 0) nodeflow[nodeu] -= arcflow[i];
                                            nodei[i] = nodeu;
                                        }
                                        nodeflow[source] = nodeflow[sink];
                                        finish = true;
                                        continue entry1;
                                    }
                                    minimumcut[sink] = 1;
                                }
                                while (true) {
                                    if (!controlc) {
                                        in--;
                                        if (in == 0) break;
                                        nodeu = imap[in];
                                        nodep = firstarc[nodeu] - 1;
                                        nodeq = jmap[nodeu] - 1;
                                    }
                                    controlc = false;
                                    while (nodep != nodeq) {
                                        nodev = nodej[nodeq];
                                        if ((minimumcut[nodev] <= 0) || 
                                                (capacity[nodeq] == arcflow[nodeq])) {
                                            nodeq--;
                                            continue;
                                        } else {
                                            nodej[nodeq] = -nodev;
                                            capacity[nodeq] -= arcflow[nodeq];
                                            arcflow[nodeq] = 0;
                                            nodep++;
                                            if (nodep < nodeq) {
                                                nodei[nodei[nodep]] = nodeq;
                                                nodei[nodei[nodeq]] = nodep;
                                                controlb = true;
                                                continue entry2;
                                            }
                                            break;
                                        }
                                    }
                                    if (nodep >= firstarc[nodeu]) minimumcut[nodeu] = nodep;
                                    
                                }
                                nodex = 0;
                                for (i = 1; i <= iout; i++)
                                    if (minimumcut[imap[i]] > 0) {
                                        nodex++;
                                        imap[nodex] = imap[i];
                                    }
                                flag = -1;
                                nodey = 1;
                            }
                            entry4:
                                while (true) {
                                    if (!controlg) {
                                        if (!controlf) {
                                            if (!controld && !controle)
                                                nodeu = imap[nodey];
                                            if ((nodeflow[nodeu] <= 0) || controld || controle) {
                                                if (!controle) {
                                                    controld = false;
                                                    nodey++;
                                                    if (nodey <= nodex)
                                                        continue entry4;
                                                    parm = 0;
                                                }
                                                controle = false;
                                                nodey--;
                                                if (nodey != 1) {
                                                    nodeu = imap[nodey];
                                                    if (nodeflow[nodeu] < 0) {
                                                        controle = true;
                                                        continue entry4;
                                                    }
                                                    if (nodeflow[nodeu] == 0) {
                                                        medge = m + 1;
                                                        if (nodeu < n) medge = firstarc[nodeu + 1];
                                                        last = jmap[nodeu];
                                                        jmap[nodeu] = medge;
                                                        while (true) {
                                                            if (last == medge) {
                                                                controle = true;
                                                                continue entry4;
                                                            }
                                                            j = nodei[last];
                                                            curflow = arcflow[j];
                                                            arcflow[j] = 0;
                                                            capacity[j] -= curflow;
                                                            arcflow[last] -= curflow;
                                                            last++;
                                                        }
                                                    }
                                                    if (firstarc[nodeu] > minimumcut[nodeu]) {
                                                        last = jmap[nodeu];
                                                        do {
                                                            j = nodei[last];
                                                            curflow = arcflow[j];
                                                            if (nodeflow[nodeu] < curflow) curflow = nodeflow[nodeu];
                                                            arcflow[j] -= curflow;
                                                            nodeflow[nodeu] -= curflow;
                                                            nodev = nodej[last];
                                                            nodeflow[nodev] += curflow;
                                                            last++;
                                                        } while (nodeflow[nodeu] > 0);
                                                        nodeflow[nodeu] = -1;
                                                        controle = true;
                                                        continue entry4;
                                                    }
                                                    last = minimumcut[nodeu] + 1;
                                                    controlf = true;
                                                    continue entry4;
                                                }
                                                for (i = 1; i <= m; i++) {
                                                    nodev = -nodej[i];
                                                    if (nodev >= 0) {
                                                        nodej[i] = nodev;
                                                        j = nodei[i];
                                                        capacity[i] -= arcflow[j];
                                                        curflow = arcflow[i] - arcflow[j];
                                                        arcflow[i] = curflow;
                                                        arcflow[j] = -curflow;
                                                        
                                                    }
                                                }
                                                continue entry3;
                                            }
                                            last = minimumcut[nodeu] + 1;
                                        }
                                    }
                                    while (true) {
                                        if (!controlg) {
                                            controlf = false;
                                            last--;
                                            if (last < firstarc[nodeu]) break;
                                            nodev = -nodej[last];
                                            if (nodeflow[nodev] < 0) continue;
                                            curflow = capacity[last] - arcflow[last];
                                            if (nodeflow[nodeu] < curflow) curflow = nodeflow[nodeu];
                                            arcflow[last] += curflow;
                                            nodeflow[nodeu] -= curflow;
                                            nodeflow[nodev] += curflow;
                                            parm = 1;
                                            nodep = nodei[last];
                                            nodeq = jmap[nodev] - 1;
                                            if (nodep < nodeq) {
                                                nodei[nodei[nodep]] = nodeq;
                                                nodei[nodei[nodeq]] = nodep;
                                                controlb = true;
                                                continue entry2;
                                            }
                                            if (nodep == nodeq) jmap[nodev] = nodeq;
                                        }
                                        controlg = false;
                                        if (nodeflow[nodeu] > 0) continue;
                                        if (capacity[last] == arcflow[last]) last--;
                                        break;
                                    }
                                    minimumcut[nodeu] = last;
                                    if (parm != 0) {
                                        controld = true;
                                        continue entry4;
                                    }
                                    last = jmap[nodeu];
                                    do {
                                        j = nodei[last];
                                        curflow = arcflow[j];
                                        if (nodeflow[nodeu] < curflow) curflow = nodeflow[nodeu];
                                        arcflow[j] -= curflow;
                                        nodeflow[nodeu] -= curflow;
                                        nodev = nodej[last];
                                        nodeflow[nodev] += curflow;
                                        last++;
                                    } while (nodeflow[nodeu] > 0);
                                    nodeflow[nodeu] = -1;
                                    controle = true;
                                    continue entry4;
                                }
                        }
                }
            }
    }
    

  

}
