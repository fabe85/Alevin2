package vnreal.evaluations.metrics;


import vnreal.evaluations.metrics.utils.DhbIllegalDimension;
import vnreal.evaluations.metrics.utils.DhbNonSymmetricComponents;

import vnreal.evaluations.metrics.utils.JacobiTransformation;

import vnreal.evaluations.metrics.utils.MatrixConverter;
import vnreal.evaluations.metrics.utils.SymmetricMatrix;


/**
 * This class is to calculate the algebraicConnectivity only of the virtual
 * network considering all hidden hops.
 * 
 * @author baskefab
 *
 */
public class AlgebraicConnectivity0 extends AbstractEvaluation {
    
    private double[][] hmatrix;

    @Override
    public double calculate() {
        
        int[][] matrix = MatrixConverter.generateVirtualMatrix(stack);
        
        this.hmatrix = new double[matrix.length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                this.hmatrix[i][j] = matrix[i][j];
            }
        }
        SymmetricMatrix sMatrix = new SymmetricMatrix(this.hmatrix);
        try {
            sMatrix = SymmetricMatrix.fromComponents(this.hmatrix);
        } catch(DhbNonSymmetricComponents e) {
            return 0;
        } catch(DhbIllegalDimension e) {
            return 0;
        }
        JacobiTransformation jacobi = new JacobiTransformation(sMatrix);
        jacobi.evaluate();
        double[] eigenvalues = jacobi.eigenvalues();
        double largestEigenvalue = eigenvalues[0];   
        int positionOfLargestEigen = 0;
        for (int i = 1; i < eigenvalues.length; i++) {
            if (eigenvalues[i] > largestEigenvalue) {
                largestEigenvalue = eigenvalues[i];
                positionOfLargestEigen = i;
            }
        }
        
        double secondLargestEigenvalue;
        if (positionOfLargestEigen == 0) {
            secondLargestEigenvalue = eigenvalues[1];
        } else  if (positionOfLargestEigen == eigenvalues.length - 1) {
            secondLargestEigenvalue = eigenvalues[0];
        } else {
            secondLargestEigenvalue = eigenvalues[positionOfLargestEigen - 1];
        }
        
        double[] secondEigen = new double[eigenvalues.length - 1];
        for (int i = 0; i < secondEigen.length; i++) {
            if (i < positionOfLargestEigen) {
                secondEigen[i] = eigenvalues[i];
            } else {
                secondEigen[i] = eigenvalues[i + 1];
            }
        }
        
        for (int i = 0; i < secondEigen.length; i++) {
            if (secondEigen[i] > secondLargestEigenvalue) {
                secondLargestEigenvalue = secondEigen[i];
            }
        }
        return secondLargestEigenvalue;

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
            this.hmatrix = new double[matrix.length][matrix.length];
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    this.hmatrix[i][j] = matrix[i][j];
                }
            }
            SymmetricMatrix sMatrix = new SymmetricMatrix(this.hmatrix);
            try {
                sMatrix = SymmetricMatrix.fromComponents(this.hmatrix);
            } catch(DhbNonSymmetricComponents e) {
                return 0;
            } catch(DhbIllegalDimension e) {
                return 0;
            }
            JacobiTransformation jacobi = new JacobiTransformation(sMatrix);
            jacobi.evaluate();
            double[] eigenvalues = jacobi.eigenvalues();
            double largestEigenvalue = eigenvalues[0];   
            int positionOfLargestEigen = 0;
            for (int i = 1; i < eigenvalues.length; i++) {
                if (eigenvalues[i] > largestEigenvalue) {
                    largestEigenvalue = eigenvalues[i];
                    positionOfLargestEigen = i;
                }
            }
            
            double secondLargestEigenvalue;
            if (positionOfLargestEigen == 0) {
                secondLargestEigenvalue = eigenvalues[1];
            } else  if (positionOfLargestEigen == eigenvalues.length - 1) {
                secondLargestEigenvalue = eigenvalues[0];
            } else {
                secondLargestEigenvalue = eigenvalues[positionOfLargestEigen - 1];
            }
            
            double[] secondEigen = new double[eigenvalues.length - 1];
            for (int i = 0; i < secondEigen.length; i++) {
                if (i < positionOfLargestEigen) {
                    secondEigen[i] = eigenvalues[i];
                } else {
                    secondEigen[i] = eigenvalues[i + 1];
                }
            }
            
            for (int i = 0; i < secondEigen.length; i++) {
                if (secondEigen[i] > secondLargestEigenvalue) {
                    secondLargestEigenvalue = secondEigen[i];
                }
            }
            result += secondLargestEigenvalue;
        }
        
        return result/counter;
        */
       
        /*
        int[][] matrix = MatrixConverter.generateAugmentedGraph(stack);
        this.hmatrix = new double[matrix.length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                this.hmatrix[i][j] = matrix[i][j];
            }
        }
        
        
        Matrix matrixX = new Matrix(hmatrix);
        LargestEigenvalueFinder finder = new LargestEigenvalueFinder(0.001, matrixX); //0.001
        finder.evaluate();
        LargestEigenvalueFinder nextFinder = finder.nextLargestEigenvalueFinder();
        nextFinder.evaluate();
        
        return nextFinder.eigenvalue();
        */
        
        /*
        SymmetricMatrix sMatrix = new SymmetricMatrix(this.hmatrix);
        try {
            sMatrix = SymmetricMatrix.fromComponents(this.hmatrix);
        } catch(DhbNonSymmetricComponents e) {
            return 0;
        } catch(DhbIllegalDimension e) {
            return 0;
        }
        JacobiTransformation jacobi = new JacobiTransformation(sMatrix);
        jacobi.evaluate();
        double[] eigenvalues = jacobi.eigenvalues();
        double largestEigenvalue = eigenvalues[0];   
        int positionOfLargestEigen = 0;
        for (int i = 1; i < eigenvalues.length; i++) {
            if (eigenvalues[i] > largestEigenvalue) {
                largestEigenvalue = eigenvalues[i];
                positionOfLargestEigen = i;
            }
        }
        
        double secondLargestEigenvalue;
        if (positionOfLargestEigen == 0) {
            secondLargestEigenvalue = eigenvalues[1];
        } else  if (positionOfLargestEigen == eigenvalues.length - 1) {
            secondLargestEigenvalue = eigenvalues[0];
        } else {
            secondLargestEigenvalue = eigenvalues[positionOfLargestEigen - 1];
        }
        
        double[] secondEigen = new double[eigenvalues.length - 1];
        for (int i = 0; i < secondEigen.length; i++) {
            if (i < positionOfLargestEigen) {
                secondEigen[i] = eigenvalues[i];
            } else {
                secondEigen[i] = eigenvalues[i + 1];
            }
        }
        
        for (int i = 0; i < secondEigen.length; i++) {
            if (secondEigen[i] > secondLargestEigenvalue) {
                secondLargestEigenvalue = secondEigen[i];
            }
        }
        
        return secondLargestEigenvalue;*/
        
        
        /*
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == 1) {
                    counter++;
                }
            }
        }
            return counter;*/
        
    }

   

    public String toString() {
        return ("AlgebraicConnectivity0");
    }
    
   
    
    
}