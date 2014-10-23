package vnreal.evaluations.metrics.utils;

public class DhbVector
{
        protected double[] components;

/**
 * Create a vector of given dimension.
 * NOTE: The supplied array of components must not be changed.
 * @param comp double[]
 */
public DhbVector(  double comp[]) throws NegativeArraySizeException
{
        int n = comp.length;
        if ( n <= 0 )
                throw new NegativeArraySizeException(
                                                                "Vector components cannot be empty");
        components = new double[n];
        System.arraycopy( comp, 0, components, 0, n);
}
/**
 * Create a vector of given dimension.
 * @param dimension int dimension of the vector; must be positive.
 */
public DhbVector ( int dimension) throws NegativeArraySizeException
{
        if ( dimension <= 0 )
                throw new NegativeArraySizeException(
                                                                "Requested vector size: "+dimension);
        components = new double[dimension];
        clear();
}
/**
 * @param v DHBmatrixAlgebra.DhbVector
 * @exception DHBmatrixAlgebra.DhbIllegalDimension if the vector
 * and supplied vector do not have the same dimension.
 */
public void accumulate ( double[] x) throws DhbIllegalDimension
{
        if ( this.dimension() != x.length )
                throw new DhbIllegalDimension("Attempt to add a "
                                        +this.dimension()+"-dimension vector to a "
                                                                        +x.length+"-dimension array");
        for ( int i = 0; i < this.dimension(); i++)
                components[i] += x[i];
}
/**
 * @param v DHBmatrixAlgebra.DhbVector
 * @exception DHBmatrixAlgebra.DhbIllegalDimension if the vector
 * and supplied vector do not have the same dimension.
 */
public void accumulate ( DhbVector v) throws DhbIllegalDimension
{
        if ( this.dimension() != v.dimension() )
                throw new DhbIllegalDimension("Attempt to add a "
                                        +this.dimension()+"-dimension vector to a "
                                                                +v.dimension()+"-dimension vector");
        for ( int i = 0; i < this.dimension(); i++)
                components[i] += v.components[i];
}
/**
 * @param v DHBmatrixAlgebra.DhbVector
 * @exception DHBmatrixAlgebra.DhbIllegalDimension if the vector
 * and supplied vector do not have the same dimension.
 */
public void accumulateNegated( double[] x) throws DhbIllegalDimension
{
        if ( this.dimension() != x.length )
                throw new DhbIllegalDimension("Attempt to add a "
                                                +this.dimension()+"-dimension vector to a "
                                                                                +x.length+"-dimension array");
        for ( int i = 0; i < this.dimension(); i++)
                components[i] -= x[i];
}
/**
 * @param v DHBmatrixAlgebra.DhbVector
 * @exception DHBmatrixAlgebra.DhbIllegalDimension if the vector
 * and supplied vector do not have the same dimension.
 */
public void accumulateNegated( DhbVector v) throws DhbIllegalDimension
{
        if ( this.dimension() != v.dimension() )
                throw new DhbIllegalDimension("Attempt to add a "
                                                +this.dimension()+"-dimension vector to a "
                                                                +v.dimension()+"-dimension vector");
        for ( int i = 0; i < this.dimension(); i++)
                components[i] -= v.components[i];
}
/**
 * @return DHBmatrixAlgebra.DhbVector sum of the vector with
 *                                                                                              the supplied vector
 * @param v DHBmatrixAlgebra.DhbVector
 * @exception DHBmatrixAlgebra.DhbIllegalDimension if the vector
 *                              and supplied vector do not have the same dimension.
 */
public DhbVector add ( DhbVector v) throws DhbIllegalDimension
{
        if ( this.dimension() != v.dimension() )
                throw new DhbIllegalDimension("Attempt to add a "
                                                +this.dimension()+"-dimension vector to a "
                                                                +v.dimension()+"-dimension vector");
        double[] newComponents = new double[ this.dimension()];
        for ( int i = 0; i < this.dimension(); i++)
                newComponents[i] = components[i] + v.components[i];
        return new DhbVector( newComponents);
}
/**
 * Sets all components of the receiver to 0.
 */
public void clear()
{
        for ( int i = 0; i < components.length; i++) components[i] = 0;
}
/**
 * @return double
 * @param n int
 */
public double component( int n)
{
        return components[n];
}
/**
 * Returns the dimension of the vector.
 * @return int
 */
public int dimension ( )
{
        return components.length;
}
/**
 * @return true if the supplied vector is equal to the receiver
 * @param v DHBmatrixAlgebra.DhbVector
 */
public boolean equals( DhbVector v)
{
        int n = this.dimension();
        if ( v.dimension() != n )
                return false;
        for ( int i = 0; i < n; i++)
        {
                if ( v.components[i] != components[i] )
                        return false;
        }       
        return true;
}
/**
 * Computes the norm of a vector.
 */
public double norm ( )
{
        double sum = 0;
        for ( int i = 0; i < components.length; i++)
                sum += components[i]*components[i];
        return Math.sqrt( sum);
}
/**
 * @param x double
 */
public DhbVector normalizedBy ( double x )
{
        for ( int i = 0; i < this.dimension(); i++)
                components[i] /= x;
        return this;
}
/**
 * Computes the product of the vector by a number.
 * @return DHBmatrixAlgebra.DhbVector
 * @param d double
 */
public DhbVector product( double d)
{
        double newComponents[] = new double[components.length];
        for ( int i = 0; i < components.length; i++)
                newComponents[i] = d * components[i];
        return new DhbVector(newComponents);
}
/**
 * Compute the scalar product (or dot product) of two vectors.
 * @return double the scalar product of the receiver with the argument
 * @param v DHBmatrixAlgebra.DhbVector
 * @exception DHBmatrixAlgebra.DhbIllegalDimension if the dimension
 *                                                                                              of v is not the same.
 */
public double product ( DhbVector v) throws DhbIllegalDimension
{
        int n = v.dimension();
        if ( components.length != n )
                throw new DhbIllegalDimension(
                                        "Dot product with mismatched dimensions: "
                                        +components.length+", "+n);
        return secureProduct( v);
}
/**
 * Computes the product of the transposed vector with a matrix
 * @return MatrixAlgebra.DhbVector
 * @param a MatrixAlgebra.Matrix
 */
public DhbVector product ( Matrix a) throws DhbIllegalDimension
{
        int n = a.rows();
        int m = a.columns();
        if ( this.dimension() != n )
                throw new DhbIllegalDimension(
                                        "Product error: transposed of a "+this.dimension()
                                        +"-dimension vector cannot be multiplied with a "
                                                                                        +n +" by "+m+" matrix");
        return secureProduct( a);
}
/**
 * @param x double
 */
public DhbVector scaledBy ( double x )
{
        for ( int i = 0; i < this.dimension(); i++)
                components[i] *= x;
        return this;
}
/**
 * Compute the scalar product (or dot product) of two vectors.
 * No dimension checking is made.
 * @return double the scalar product of the receiver with the argument
 * @param v DHBmatrixAlgebra.DhbVector
 */
protected double secureProduct ( DhbVector v)
{
        double sum = 0;
        for ( int i = 0; i < v.dimension(); i++)
                sum += components[i]*v.components[i];
        return sum;
}
/**
 * Computes the product of the transposed vector with a matrix
 * @return MatrixAlgebra.DhbVector
 * @param a MatrixAlgebra.Matrix
 */
protected DhbVector secureProduct ( Matrix a) 
{
        int n = a.rows();
        int m = a.columns();
        double[] vectorComponents = new double[m];
        for ( int j = 0; j < m; j++ )
        {
                vectorComponents[j] = 0;
                for ( int i = 0; i < n; i++)
                        vectorComponents[j] += components[i] * a.components[i][j];
        }       
        return new DhbVector( vectorComponents);
}
/**
 * @return DHBmatrixAlgebra.DhbVector   subtract the supplied vector
 *                                                                                                      to the receiver
 * @param v DHBmatrixAlgebra.DhbVector
 * @exception DHBmatrixAlgebra.DhbIllegalDimension if the vector
 * and supplied vector do not have the same dimension.
 */
public DhbVector subtract ( DhbVector v) throws DhbIllegalDimension
{
        if ( this.dimension() != v.dimension() )
                throw new DhbIllegalDimension("Attempt to add a "
                                                +this.dimension()+"-dimension vector to a "
                                                                +v.dimension()+"-dimension vector");
        double[] newComponents = new double[ this.dimension()];
        for ( int i = 0; i < this.dimension(); i++)
                newComponents[i] = components[i] - v.components[i];
        return new DhbVector( newComponents);
}
/**
 * @return MatrixAlgebra.Matrix tensor product with the specified
 *                                                                                                                              vector
 * @param v MatrixAlgebra.DhbVector     second vector to build tensor
 *                                                                                                              product with.
 */
public Matrix tensorProduct ( DhbVector v)
{
        int n = dimension();
        int m = v.dimension();
        double [][] newComponents = new double[n][m];
        for ( int i = 0; i < n; i++)
        {
                for ( int j = 0; j < m; j++)
                        newComponents[i][j] = components[i] * v.components[j];
        }
        return n == m ? new SymmetricMatrix( newComponents)
                                                  : new Matrix( newComponents);
}
/**
 * @return double[]     a copy of the components of the receiver.
 */
public double[] toComponents ( )
{
        int n = dimension();
        double[] answer = new double[ n];
        System.arraycopy( components, 0, answer, 0, n);
        return answer;
}
/**
 * Returns a string representation of the vector.
 * @return java.lang.String
 */
public String toString()
{
        StringBuffer sb = new StringBuffer();
        char[] separator = { '[', ' '};
        for ( int i = 0; i < components.length; i++)
        {
                sb.append( separator);
                sb.append( components[i]);
                separator[0] = ',';
        }
        sb.append(']');
        return sb.toString();
}
}