package vnreal.evaluations.metrics;

import vnreal.evaluations.metrics.utils.DhbIllegalDimension;
import vnreal.evaluations.metrics.utils.DhbNonSymmetricComponents;
import vnreal.evaluations.metrics.utils.JacobiTransformation;
import vnreal.evaluations.metrics.utils.MatrixConverter;
import vnreal.evaluations.metrics.utils.SymmetricMatrix;

/**
 * This class is to calculate the algebraic connectivity of the network 
 * represented as an augmented graph matrix.
 * 
 * @author baskefab
 *
 */
public class AlgebraicConnectivity extends AbstractEvaluation {
    private double[][] hmatrix;

    @Override
    public double calculate() {
        
        //the matrix representing the augmented graph
        int[][] matrix = MatrixConverter.generateAugmentedGraph(stack); 
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
        
        
        
    }

    @Override
    public String toString() {
        return ("AlgebraicConnectivity");
    }

}
