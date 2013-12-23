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
import java.util.*;

/**
 * Two-dimensional matrix.
 */
public class Vector {
    double [] data;

    public Vector(double[] input) {
        this.data = input;
    }

    public Vector(int[] input) {
        data = new double[input.length];
        for(int i=0; i<input.length;++i){
            data[i] = (double )input[i];
        }
    }

    public Vector(int length) {
        data = new double[length];
        Arrays.fill(data, 0d);
    }


    public void set(int i, double v){
        data[i]= v;
    }

    public int getLength(){
        return data.length;
    }

    public Vector product(Vector v2) {
        if (getLength() != v2.getLength()) {
            throw new IllegalArgumentException("Vectors are not of the same size");
        }
        double[] result = new double[getLength()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = data[i] * v2.data[i];
        }
        return new Vector(result);
    }

    public Matrix outer(Vector other){
        return new Matrix(outer(data, other.data));
    }
    public Vector dot(Matrix matrix){
        return new Vector(dot(data, matrix.data));
    }

    public boolean closeTo(Vector other, double within){
        if(getLength()!=other.getLength()){
            throw new  IllegalArgumentException();
        }
        double eps=0d;
        for(int i=0; i< other.getLength();++i){
            eps += Math.abs(data [i]-other.data [i]);
        }
        eps/=getLength();
        return eps<within;
    }
    public double max(){
        double max = -Double.MAX_VALUE;
        int m=-1;
        for(double d: data){
            if(d>=max){
                max=d;
            }
        }
        return max;
    }
    public Vector add(double value) {
        double[]  result = new double[data.length];
        for (int c = 0; c < data.length; ++c) {
            result [c] = data[c] + value;
        }
        return new Vector(result);
    }
    public Vector add(Vector other) {
        double[]  result = new double[data.length];
        for (int c = 0; c < data.length; ++c) {
                result [c] = data[c] + other.data[c];
        }
        return new Vector(result);
    }

     /**
     * @return outer product of 2 vectors
     */
    public double [][] outer(double [] a, double [] b){
        double  [][] result = new double[a.length][];
        for(int i=0; i< a.length; ++i){
            result[i] = new double[b.length];
            for(int j=0; j<b.length;++j){
                result[i][j] = a[i] *b[j] ;
            }
        }
        return result ;
    }

    private double[] dot(double[] m1, double[][] m2) {
        if(m1.length != m2 .length){
            throw new IllegalArgumentException("Length of vector " + m1.length
            + " does not match the number of matrix rows " + m2 .length);
        }
        double [] result = new double[m2[0].length];
        Arrays.fill(result, 0d);

        for (int c = 0; c < m2[0].length; ++c) {
            for(int i=0; i< m1.length;++i){
                result[c] += m1[i] *m2[i][c];
            }
        }
        double [] app =  new double[m2[0].length];

        for(int i=0; i<m2[0].length;++i){
            app[i] = result[i];
        }
        return app;
    }

    public Vector div(Vector v2) {
        if(getLength()!=v2.getLength()){
            throw new IllegalArgumentException("Vectors are not aligned");
        }
        double[] result  = new double[getLength()];
        for (int i = 0; i < result.length; ++i) {
            result[i] =   data[i]/v2.data[i];
        }
        return new Vector(result);
    }

    @Override
    public String toString() {
        return Arrays.toString(data);
    }

    public double elementAt(int i) {
        return data[i];
    }

}
