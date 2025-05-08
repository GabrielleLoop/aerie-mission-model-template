package missionmodel;

public class RealMatrix {
    private final int rows;
    private final int cols;
    private final double[][] data; // double double this this, double double that that

    // boa constructor
    public RealMatrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.data = new double[rows][cols];
    }

    // fill this matrix with the given values
    public RealMatrix(double[][] input) {
        this.rows = input.length;
        this.cols = input[0].length;
        this.data = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            if (input[i].length != cols)
                throw new IllegalArgumentException("All rows must have the same length.");
            System.arraycopy(input[i], 0, data[i], 0, cols);
        }
    }

    // identity (the equivalent of eye)
    public static RealMatrix identity(int size) {
        RealMatrix I = new RealMatrix(size, size);
        for (int i = 0; i < size; i++) {
            I.data[i][i] = 1.0;
        }
        return I;
    }

    // transpose rows and columns
    public RealMatrix transpose() {
        RealMatrix result = new RealMatrix(cols, rows);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result.data[j][i] = data[i][j];
        return result;
    }

    // add another matrix to this matrix
    public RealMatrix add(RealMatrix B) {
        if (rows != B.rows || cols != B.cols)
            throw new IllegalArgumentException("Matrix dimensions must agree.");
        RealMatrix result = new RealMatrix(rows, cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result.data[i][j] = this.data[i][j] + B.data[i][j];
        return result;
    }

    // subtract another matrix from this matrix
    public RealMatrix subtract(RealMatrix B) {
        if (rows != B.rows || cols != B.cols)
            throw new IllegalArgumentException("Matrix dimensions must agree.");
        RealMatrix result = new RealMatrix(rows, cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result.data[i][j] = this.data[i][j] - B.data[i][j];
        return result;
    }

    // multiply this matrix by another matrix
    public RealMatrix multiply(RealMatrix B) {
        if (this.cols != B.rows)
            throw new IllegalArgumentException("Inner dimensions must match.");
        RealMatrix result = new RealMatrix(this.rows, B.cols);
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < B.cols; j++) {
                for (int k = 0; k < this.cols; k++) {
                    result.data[i][j] += this.data[i][k]*B.data[k][j];
                }
            }
        }
        return result;
    }

    // multiply this matrix by a scalar
    public RealMatrix scale(double scalar) {
        RealMatrix result = new RealMatrix(rows, cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result.data[i][j] = scalar*this.data[i][j];
        return result;
    }

    // convert the matrix to a 2D array
    public double[][] toArray() {
        return data;
    }

    // get a specific matrix element
    public double get(int row, int col) {
        return data[row][col];
    }

    // set a specific matrix element
    public void set(int row, int col, double value) {
        data[row][col] = value;
    }

    // get the number of rows
    public int getRowCount() { return rows; }

    // get the number of columns
    public int getColCount() { return cols; }

    // override toString so it prints nicely
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (double[] row : data) {
            sb.append("[ ");
            for (double val : row) {
                sb.append(String.format("%.4f ", val));
            }
            sb.append("]\n");
        }
        return sb.toString();
    }
}
