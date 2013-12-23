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


import java.util.Arrays;

public class Matrix {
    //rows x columns
    final double[][] data;

    public Matrix(double[][] data) {
        this.data = data;
    }


    public Matrix(double[] input) {
        this.data = new double[1][];
        this.data[0] = input;
    }


    public int getNumberOfColumns() {
        return data[0].length;
    }

    public int getNumberOfRows() {
        return data.length;
    }

    public void setRow(int row, Vector replacement) {
        this.data[row] = replacement.data;
    }

    /**
     * Adds value to each element
     *
     * @param value - value to add
     * @return new matrix
     */
    public Matrix add(double value) {
        double[][] result = new double[data.length][];
        for (int r = 0; r < data.length; ++r) {
            result[r] = new double[data[0].length];
            for (int c = 0; c < data[0].length; ++c) {
                result[r][c] = data[r][c] + value;
            }
        }
        return new Matrix(result);
    }

    public void incrementColumns(int[] columns, Matrix inc) {
        if (getNumberOfRows() != inc.getNumberOfRows()) {
            throw new IllegalArgumentException("Incompatible number of rows");
        }
        for (int r = 0; r < data.length; ++r) {
            for (int c = 0; c < columns.length; ++c) {
                data[r][columns[c]] += inc.data[r][c];
            }
        }
    }


    /**
     * Multiplies each element by the value
     *
     * @param value the multiplier
     * @return modified matrix
     */
    public Matrix product(double value) {
        double[][] result = new double[data.length][];
        for (int r = 0; r < data.length; ++r) {
            result[r] = new double[data[0].length];
            for (int c = 0; c < data[0].length; ++c) {
                result[r][c] = data[r][c] * value;
            }
        }
        return new Matrix(result);
    }

    public Matrix product(Matrix other) {
        double[][] product = product(this.data, other.data);
        return new Matrix(product);
    }



    /**
     * @return vector of sums over each row
     */
    public Vector sumByRows() {
        double[] result = new double[data.length];
        for (int r = 0; r < data.length; ++r) {
            double _r = 0d;
            for (double d : data[r]) {
                _r += d;
            }
            result[r] = _r;
        }

        return new Vector(result);
    }

    /**
     * Build a matrix with the same number of rows and columns as
     * an original one, and filled with 0es.
     *
     * @return new matrix of the same shape filled with )es
     */
    public Matrix shape() {
        int numRows = data.length;
        int numCols = data[0].length;
        double[][] array = new double[numRows][];

        for (int k = 0; k < numRows; ++k) {
            array[k] = new double[numCols];
            Arrays.fill(array[k], 0.0d);
        }
        return new Matrix(array);
    }

    public Matrix tr() {
        int numRows = data.length;
        int numCols = data[0].length;
        double[][] result = new double[numCols][];
        for (int r = 0; r < numCols; ++r) {
            result[r] = new double[numRows];
            for (int c = 0; c < numRows; ++c) {
                result[r][c] = data[c][r];
            }
        }
        return new Matrix(result);
    }

    public Vector getRow(int row) {
        return new Vector(data[row]);
    }

    public Matrix add(Matrix other) {
        double[][] result = new double[data.length][];

        for (int r = 0; r < data.length; ++r) {
            result[r] = new double[data[0].length];

            for (int c = 0; c < data[0].length; ++c) {
                result[r][c] = data[r][c] + other.data[r][c];
            }
        }
        return new Matrix(result);


    }

    public Vector extractColumn(int colNum) {
        double[] result = new double[data.length];
        for (int r = 0; r < data.length; ++r) {
            result[r] = data[r][colNum];
        }
        return new Vector(result);
    }

    public Matrix extractColumns(int[] colNum) {
        double[][] result = new double[data.length][];
        for (int r = 0; r < data.length; ++r) {
            double [] projection =  new double[colNum.length];
            for(int c=0; c<colNum.length;++c){
                projection[c] = data[r][colNum[c]];
            }
            result[r] = projection;
        }
        return new Matrix(result);
    }


    double[][] product(double[][] m1, double[][] m2) {
        if (m1.length != m2.length) {
            throw new IllegalArgumentException("Numbers of rows does not match");
        }
        if (m1[0].length != m2[0].length) {
            throw new IllegalArgumentException("Number of columns do not match");
        }

        double[][] result = new double[m1.length][];
        int numCols = m2[0].length;

        for (int r = 0; r < m1.length; ++r) {
            result[r] = new double[numCols];
            for (int c = 0; c < numCols; ++c) {
                result[r][c] = m1[r][c] * m2[r][c];
            }
        }
        return result;
    }

    double[][] dot(double[][] m1, double[][] m2) {
        if (m1[0].length != m2.length) {
            throw new IllegalArgumentException("Numbers of columns does not match number of rows");
        }
        if (m1.length != m2[0].length) {
            throw new IllegalArgumentException("Number of rows do not match the number of columns");
        }
        double[][] result = new double[m1.length][];
        for (int r = 0; r < m1.length; ++r) {
            result[r] = new double[m2[0].length];
            for (int c = 0; c < m2[0].length; ++c) {
                result[r][c] = 0d;
                for (int d = 0; d < m2.length; ++d) {
                    result[r][c] += m1[r][d] * m2[d][c];
                }
            }
        }
        return result;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (double[] d : data) {
            sb.append(Arrays.toString(d)).append('\n');
        }
        return sb.toString();

    }

}
