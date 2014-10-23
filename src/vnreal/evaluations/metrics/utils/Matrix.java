package vnreal.evaluations.metrics.utils;

public class Matrix {

    protected double[][] components;
    protected LUPDecomposition lupDecomposition = null;

/**
* Creates a matrix with given components.
* NOTE: the components must not be altered after the definition.
* @param a double[][]
*/
public Matrix ( double[][] a)
{
    components = a;
}
/**
* Creates a null matrix of given dimensions.
* @param n int number of rows
* @param m int number of columns
* @exception NegativeArraySizeException
*/
public Matrix ( int n, int m) throws NegativeArraySizeException
{
    if ( n <= 0 || m <= 0)
            throw new NegativeArraySizeException(
                                                            "Requested matrix size: "+n+" by "+m);
    components = new double[n][m];
    clear();        
}
/**
* @param a MatrixAlgebra.Matrix
* @exception MatrixAlgebra.DhbIllegalDimension if the supplied matrix
*                                                                      does not have the same dimensions.
*/
public void accumulate ( Matrix a) throws DhbIllegalDimension
{
    if ( a.rows() != rows() || a.columns() != columns() )
            throw new DhbIllegalDimension("Operation error: cannot add a"
                                                            +a.rows()+" by "+a.columns()
                                                                    +" matrix to a "+rows()+" by "
                                                                                            +columns()+" matrix");
    int m = components[0].length;
    for ( int i = 0; i < components.length; i++)
    {
            for ( int j = 0; j < m; j++)
                    components[i][j] += a.component(i,j);
    }       
}
/**
* @return MatrixAlgebra.Matrix         sum of the receiver with the
*                                                                                                      supplied matrix.
* @param a MatrixAlgebra.Matrix
* @exception MatrixAlgebra.DhbIllegalDimension if the supplied matrix
*                                                                      does not have the same dimensions.
*/
public Matrix add ( Matrix a) throws DhbIllegalDimension
{
    if ( a.rows() != rows() || a.columns() != columns() )
            throw new DhbIllegalDimension("Operation error: cannot add a"
                                                                    +a.rows()+" by "+a.columns()
                                                                            +" matrix to a "+rows()+" by "
                                                                                            +columns()+" matrix");
    return new Matrix( addComponents( a));
}
/**
* Computes the components of the sum of the receiver and
*                                                                                                      a supplied matrix.
* @return double[][]
* @param a MatrixAlgebra.Matrix
*/
protected double[][] addComponents ( Matrix a)
{
    int n = this.rows();
    int m = this.columns();
    double[][] newComponents = new double[n][m];
    for ( int i = 0; i < n; i++)
    {
            for ( int j = 0; j < n; j++)
                    newComponents[i][j] = components[i][j] + a.components[i][j];
    }       
    return newComponents;
}
public void clear()
{
    int m = components[0].length;
    for ( int i = 0; i < components.length; i++)
    {
            for ( int j = 0; j < m; j++) components[i][j] = 0;
    }       
}
/**
* @return int  the number of columns of the matrix
*/
public int columns ( )
{
    return components[0].length;
}
/**
* @return double
* @param n int
* @param m int
*/
public double component( int n, int m)
{
    return components[n][m];
}
/**
* @return double
* @exception MatrixAlgebra.DhbIllegalDimension if the supplied 
*                                                                                              matrix is not square.
*/
public double determinant () throws DhbIllegalDimension
{
    return lupDecomposition().determinant();
}
/**
* @return true if the supplied matrix is equal to the receiver.
* @param a MatrixAlgebra.Matrix
*/
public boolean equals( Matrix a)
{
    int n = this.rows();
    if ( a.rows() != n )
            return false;
    int m = this.columns();
    if ( a.columns() != m )
            return false;
    for ( int i = 0; i < n; i++)
    {
            for ( int j = 0; j < n; j++)
            {
                    if ( a.components[i][j] != components[i][j] )
                            return false;
            }       
    }       
    return true;
}
/**
* @return DhbMatrixAlgebra.DhbMatrix           inverse of the receiver
*                              or pseudoinverse if the receiver is not a square matrix.
* @exception java.lang.ArithmeticException if the receiver is
*                                                                                                      a singular matrix.
*/
public Matrix inverse ( ) throws ArithmeticException
{
    try { return new Matrix(
                                    lupDecomposition().inverseMatrixComponents());}
            catch ( DhbIllegalDimension e )
                    {  return new Matrix(
                                    transposedProduct().inverse()
                                                    .productWithTransposedComponents( this));}
}
/**
* @return boolean
*/
public boolean isSquare ( )
{
    return rows() == columns();
}
/**
* @return LUPDecomposition     the LUP decomposition of the receiver.
* @exception DhbIllegalDimension if the receiver is not
*                                                                                                      a square matrix.
*/
protected LUPDecomposition lupDecomposition()
                                                                            throws DhbIllegalDimension
{
    if ( lupDecomposition == null )
            lupDecomposition = new LUPDecomposition( this);
    return lupDecomposition;
}
/**
* @return MatrixAlgebra.Matrix         product of the matrix with
*                                                                                                      a supplied number
* @param a double      multiplicand.
*/
public Matrix product ( double a)
{
    return new Matrix( productComponents( a));
}
/**
* Computes the product of the matrix with a vector.
* @return DHBmatrixAlgebra.DhbVector
* @param v DHBmatrixAlgebra.DhbVector
*/
public DhbVector product ( DhbVector v) throws DhbIllegalDimension
{
    int n = this.rows();
    int m = this.columns();
    if ( v.dimension() != m )
            throw new DhbIllegalDimension("Product error: "+n+" by "+m
                    +" matrix cannot by multiplied with vector of dimension "
                                                                                                    +v.dimension());
    return secureProduct( v);
}
/**
* @return MatrixAlgebra.Matrix         product of the receiver with the
*                                                                                                      supplied matrix
* @param a MatrixAlgebra.Matrix
* @exception MatrixAlgebra.DhbIllegalDimension If the number of
*                                      columns of the receiver are not equal to the
*                                                              number of rows of the supplied matrix.
*/
public Matrix product ( Matrix a) throws DhbIllegalDimension
{
    if ( a.rows() != columns() )
            throw new DhbIllegalDimension(
                                                    "Operation error: cannot multiply a"
                                                                    +rows()+" by "+columns()
                                                                            +" matrix with a "+a.rows()
                                                                                    +" by "+a.columns()
                                                                                                            +" matrix");
    return new Matrix( productComponents( a));
}
/**
* @return double[][]
* @param a double
*/
protected double[][] productComponents ( double a)
{
    int n = this.rows();
    int m = this.columns();
    double[][] newComponents = new double[n][m];
    for ( int i = 0; i < n; i++)
    {
            for ( int j = 0; j < m; j++)
                    newComponents[i][j] = a * components[i][j];
    }       
    return newComponents;
}
/**
* @return double[][]   the components of the product of the receiver
*                                                                                      with the supplied matrix
* @param a MatrixAlgebra.Matrix
*/
protected double[][] productComponents ( Matrix a)
{
    int p = this.columns();
    int n = this.rows();
    int m = a.columns();
    double[][] newComponents = new double[n][m];
    for ( int i = 0; i < n; i++)
    {
            for ( int j = 0; j < m; j++)
            {
                    double sum = 0;
                    for ( int k = 0; k < p; k++)
                            sum += components[i][k] * a.components[k][j];
                    newComponents[i][j] = sum;
            }       
    }       
    return newComponents;
}
/**
* @return MatrixAlgebra.Matrix product of the receiver with the
*                                                                      tranpose of the supplied matrix
* @param a MatrixAlgebra.Matrix
* @exception MatrixAlgebra.DhbIllegalDimension If the number of
*                                              columns of the receiver are not equal to
*                                              the number of columns of the supplied matrix.
*/
public Matrix productWithTransposed ( Matrix a)
                                                                                    throws DhbIllegalDimension
{
    if ( a.columns() != columns() )
            throw new DhbIllegalDimension(
                                            "Operation error: cannot multiply a "+rows()
                                                    +" by "+columns()
                                                            +" matrix with the transpose of a "
                                                                    +a.rows()+" by "+a.columns()
                                                                                                            +" matrix");
    return new Matrix( productWithTransposedComponents( a));
}
/**
* @return double[][]   the components of the product of the receiver
*                                                      with the transpose of the supplied matrix
* @param a MatrixAlgebra.Matrix
*/
protected double[][] productWithTransposedComponents ( Matrix a)
{
    int p = this.columns();
    int n = this.rows();
    int m = a.rows();
    double[][] newComponents = new double[n][m];
    for ( int i = 0; i < n; i++)
    {
            for ( int j = 0; j < m; j++)
            {
                    double sum = 0;
                    for ( int k = 0; k < p; k++)
                            sum += components[i][k] * a.components[j][k];
                    newComponents[i][j] = sum;
            }       
    }       
    return newComponents;
}
/**
* @return int  the number of rows of the matrix
*/
public int rows ( )
{
    return components.length;
}
/**
* Computes the product of the matrix with a vector.
* @return DHBmatrixAlgebra.DhbVector
* @param v DHBmatrixAlgebra.DhbVector
*/
protected DhbVector secureProduct ( DhbVector v)
{
    int n = this.rows();
    int m = this.columns();
    double[] vectorComponents = new double[n];
    for ( int i = 0; i < n; i++ )
    {
            vectorComponents[i] = 0;
            for ( int j = 0; j < m; j++)
                    vectorComponents[i] += components[i][j] * v.components[j];
    }       
    return new DhbVector( vectorComponents);
}
/**
* Same as product(Matrix a), but without dimension checking.
* @return MatrixAlgebra.Matrix         product of the receiver with the
*                                                                                                      supplied matrix
* @param a MatrixAlgebra.Matrix
*/
protected Matrix secureProduct ( Matrix a)
{
    return new Matrix( productComponents( a));
}
/**
* Same as subtract ( DhbMarix a), but without dimension checking.
* @return MatrixAlgebra.Matrix
* @param a MatrixAlgebra.Matrix
*/
protected Matrix secureSubtract ( Matrix a)
{
    return new Matrix( subtractComponents( a));
}
/**
* @return MatrixAlgebra.Matrix         subtract the supplied matrix to
*                                                                                                              the receiver.
* @param a MatrixAlgebra.Matrix
* @exception MatrixAlgebra.DhbIllegalDimension if the supplied matrix
*                                                                      does not have the same dimensions.
*/
public Matrix subtract ( Matrix a) throws DhbIllegalDimension
{
    if ( a.rows() != rows() || a.columns() != columns() )
            throw new DhbIllegalDimension(
                                    "Product error: cannot subtract a"+a.rows()
                                                    +" by "+a.columns()+" matrix to a "
                                                            +rows()+" by "+columns()+" matrix");
    return new Matrix( subtractComponents( a));
}
/**
* @return double[][]
* @param a MatrixAlgebra.Matrix
*/
protected double[][] subtractComponents ( Matrix a)
{
    int n = this.rows();
    int m = this.columns();
    double[][] newComponents = new double[n][m];
    for ( int i = 0; i < n; i++)
    {
            for ( int j = 0; j < n; j++)
                    newComponents[i][j] = components[i][j] - a.components[i][j];
    }       
    return newComponents;
}
/**
* @return double[][]   a copy of the components of the receiver.
*/
public double[][] toComponents ( )
{
    int n = rows();
    int m = columns();
    double[][] answer = new double[ n][m];
    for ( int i = 0; i < n; i++ )
    {
            for ( int j = 0; j < m; j++)
                    answer[i][j] = components[i][j];
    }
    return answer;
}
/**
* Returns a string representation of the system.
* @return java.lang.String
*/
public String toString()
{
    StringBuffer sb = new StringBuffer();
    char[] separator = { '[', ' '};
    int n = rows();
    int m = columns();
    for ( int i = 0; i < n; i++)
    {
            separator[0] = '{';
            for ( int j = 0; j < m; j++)
            {
                    sb.append( separator);
                    sb.append( components[i][j]);
                    separator[0] = ' ';
            }
    sb.append('}');
    sb.append('\n');
    }
    return sb.toString();
}
/**
* @return MatrixAlgebra.Matrix         transpose of the receiver
*/
public Matrix transpose ( )
{
    int n = rows();
    int m = columns();
    double[][] newComponents = new double[m][n];
    for ( int i = 0; i < n; i++)
    {
            for( int j = 0; j < m; j++)
                    newComponents[j][i] = components[i][j];
    }       
    return new Matrix( newComponents);
}
/**
* @return DhbMatrixAlgebra.SymmetricMatrix     the transposed product
*                                                                              of the receiver with itself.
*/
public SymmetricMatrix transposedProduct()
{
    return new SymmetricMatrix( transposedProductComponents( this));
}
/**
* @return MatrixAlgebra.Matrix product of the tranpose of the
*                                                                      receiver with the supplied matrix
* @param a MatrixAlgebra.Matrix
* @exception MatrixAlgebra.DhbIllegalDimension If the number of rows
*                                                      of the receiver are not equal to 
*                                                      the number of rows of the supplied matrix.
*/
public Matrix transposedProduct ( Matrix a) throws DhbIllegalDimension
{
    if ( a.rows() != rows() )
            throw new DhbIllegalDimension(
                                    "Operation error: cannot multiply a tranposed "
                                                    +rows()+" by "+columns()
                                                            +" matrix with a "+a.rows()+" by "
                                                                                    +a.columns()+" matrix");
    return new Matrix( transposedProductComponents( a));
}
/**
* @return double[][]   the components of the product of the
*                                                                                      transpose of the receiver
* with the supplied matrix.
* @param a MatrixAlgebra.Matrix
*/
protected double[][] transposedProductComponents ( Matrix a)
{
    int p = this.rows();
    int n = this.columns();
    int m = a.columns();
    double[][] newComponents = new double[n][m];
    for ( int i = 0; i < n; i++)
    {
            for ( int j = 0; j < m; j++)
            {
                    double sum = 0;
                    for ( int k = 0; k < p; k++)
                            sum += components[k][i] * a.components[k][j];
                    newComponents[i][j] = sum;
            }       
    }       
    return newComponents;
}
}
