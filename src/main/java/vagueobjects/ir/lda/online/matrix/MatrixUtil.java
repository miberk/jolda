package vagueobjects.ir.lda.online.matrix;
/*
Copyright (c) 2013 miberk

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.GammaDistributionImpl;
import org.apache.commons.math.special.Gamma;

import java.util.Arrays;

public class MatrixUtil {
    static GammaSampling gd = new GammaSampling();

    public static double sum(Vector v){
        double s =0d;
        for(double d: v.data){
            s+=d;
        }
        return s;
    }

    public static Matrix gammaLn(Matrix m){

        int nc = m.data.length;
        int nr = m.data[0].length;
        double[][] result = new double[nc][];
        for (int k = 0; k < nc; ++k) {
            result[k] = new double[nr];
            for (int w = 0; w < nr; ++w) {
                result[k][w] = Gamma.logGamma(m.data[k][w]);
            }
        }
        return new Matrix(result);
    }

    public static Vector gammaLn(Vector v) {
       int nc = v.data.length;
       double[] result = new double[nc];
       for (int k = 0; k < nc; ++k) {
           result[k] = gammaLn(v.data[k]);
       }
       return new Vector(result);
   }


    public static double gammaLn(double d){
        return Gamma.logGamma(d);
    }


    public static double min(Matrix m){
        double s = Double.MAX_VALUE;
        for(double[] row: m.data){
            for(double  d : row){
                if(d<s){
                    s=d;
                }
            }
        }
        return s;
    }


    public static double sum(Matrix m){
        double s =0d;
        for(double[] row: m.data){
            for(double  d : row){
                s+=d;
            }
        }
        return s;
    }

    public static Vector exp(Vector vector){
        return new Vector(exp(vector.data));
    }
    public static Matrix exp(Matrix matrix){
        return new Matrix(exp(matrix.data));
    }

    private static double[][] exp(double[][] array) {
        int nc = array.length;
        int nr = array[0].length;
        double[][] result = new double[nc][];
        for (int k = 0; k < nc; ++k) {
            result[k] = new double[nr];
            for (int w = 0; w < nr; ++w) {
                result[k][w] = Math.exp(array[k][w]);
            }
        }
        return result;
    }

    public static Matrix dirichletExpectation(Matrix matrix){
        return new Matrix(dirichletExpectation(matrix.data));
    }
    public static Vector dirichletExpectation(Vector vector){
        return new Vector(dirichletExpectation(vector.data));
    }



    public static Matrix sampleGamma(int W, int K) {

        double[][] result = gd.batch(W,K ) ;
        return new Matrix(result);
    }


    static double [] dirichletExpectation(double[] array) {
        double sum=0;
        for(double d: array){
            sum += d;
        }
        double d = Gamma.digamma(sum);
        double[] result = new double[array.length];
        for(int i=0; i<array.length;++i){
            result[i] = Gamma.digamma(array[i]) - d;
        }
        return result;
    }

    /**
     * Digamma function (the first derivative of the logarithm of the gamma function).
     * @param array   - variational parameter
     * @return
     */
    static double[][] dirichletExpectation(double[][] array) {
        int numRows = array.length;
        int numCols = array[0].length;

        double[] vector = new double[numRows];
        Arrays.fill(vector, 0.0);

        for (int k = 0; k < numRows; ++k) {
            for (int w = 0; w < numCols; ++w) {
                try{
                    vector[k] += array[k][w];
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        for (int k = 0; k < numRows; ++k) {
            vector[k] = Gamma.digamma(vector[k]);
        }

        double [][] approx = new double[numRows][];
        for (int k = 0; k < numRows; ++k) {
            approx[k] = new double[numCols];
            for (int w = 0; w < numCols; ++w) {
                double z =  Gamma.digamma(array[k][w]);
                approx[k][w] = z - vector[k];
            }
        }
        return approx;
    }

    private static double[] exp(double[] array) {
        int nc = array.length;
        double[] result = new double[nc];
        for (int k = 0; k < nc; ++k) {
            result[k] = Math.exp(array[k]);
        }
        return result;
    }
    static class GammaSampling   {
        private final GammaDistributionImpl gd;
        public final static long DEFAULT_SEED = 1000000001L;
        public final static double DEFAULT_ALPHA = 100d;
        public final static double DEFAULT_BETA = 0.01d;


        public GammaSampling( ) {
            this.gd = new GammaDistributionImpl(DEFAULT_ALPHA, DEFAULT_BETA);
            this.gd.reseedRandomGenerator(DEFAULT_SEED);
        }

        double sample() {
            try {
                return gd.sample();
            } catch (MathException e) {
                throw new RuntimeException(e);
            }
        }

        public double[][] batch(int W, int K) {
            double[][] result = new double[K][];

            for (int k = 0; k < K; ++k) {
                result[k] = new double[W];
                for (int w = 0; w < W; ++w) {
                    result[k][w] = sample();
                }
            }
            return result;
        }
    }
}
