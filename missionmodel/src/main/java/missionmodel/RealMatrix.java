package missionmodel;

public class RealMatrix {
    private final int rows;
    private final int cols;
    private final double[][] data;

    // construct empty matrix with given rows and columns
    public RealMatrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.data = new double[rows][cols];
    }

    // construct matrix from 2D array
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

    // construct identity matrix
    public static RealMatrix identity(int size) {
        RealMatrix I = new RealMatrix(size, size);
        for (int i = 0; i < size; i++) {
            I.data[i][i] = 1.0;
        }
        return I;
    }

    // transpose
    public RealMatrix transpose() {
        RealMatrix result = new RealMatrix(cols, rows);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result.data[j][i] = data[i][j];
        return result;
    }

    // add matrices
    public RealMatrix add(RealMatrix B) {
        if (rows != B.rows || cols != B.cols)
            throw new IllegalArgumentException("Matrix dimensions must agree for addition.");
        RealMatrix result = new RealMatrix(rows, cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result.data[i][j] = this.data[i][j] + B.data[i][j];
        return result;
    }

    // subtract matrices
    public RealMatrix subtract(RealMatrix B) {
        if (rows != B.rows || cols != B.cols)
            throw new IllegalArgumentException("Matrix dimensions must agree for subtraction.");
        RealMatrix result = new RealMatrix(rows, cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result.data[i][j] = this.data[i][j] - B.data[i][j];
        return result;
    }

    // multiply matrices
    public RealMatrix multiply(RealMatrix B) {
        if (this.cols != B.rows)
            throw new IllegalArgumentException("Inner dimensions must match for multiplication.");
        RealMatrix result = new RealMatrix(this.rows, B.cols);
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < B.cols; j++) {
                for (int k = 0; k < this.cols; k++) {
                    result.data[i][j] += this.data[i][k] * B.data[k][j];
                }
            }
        }
        return result;
    }

    // multiply matrix by 3d vector
    public Vector3D multiply(Vector3D vector) {
        if (this.cols != 3) {
            throw new IllegalArgumentException("Matrix needs 3 columns to multiply with a Vector3D.");
        }
        
        if (this.rows != 3) {
            throw new IllegalArgumentException("Matrix needs 3 rows to produce a Vector3D result.");
        }
        
        double resultX = this.data[0][0]*vector.x + this.data[0][1]*vector.y + this.data[0][2]*vector.z;              
        double resultY = this.data[1][0]*vector.x + this.data[1][1]*vector.y + this.data[1][2]*vector.z;             
        double resultZ = this.data[2][0]*vector.x + this.data[2][1]*vector.y + this.data[2][2]*vector.z;
        
        return new Vector3D(resultX, resultY, resultZ);
    }

    // multiply matrix by scalar
    public RealMatrix scale(double scalar) {
        RealMatrix result = new RealMatrix(rows, cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result.data[i][j] = scalar * this.data[i][j];
        return result;
    }

    // matrix inversion (I'm using gauss-jordan elimination but this can be changed if needed)
    public RealMatrix invert() {
        if (rows != cols) {
            throw new IllegalArgumentException("Matrix must be square to have an inverse");
        }
        
        int n = rows;
        RealMatrix augmented = new RealMatrix(n, 2*n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                augmented.set(i, j, this.data[i][j]);
            }
            augmented.set(i, i + n, 1.0);
        }
        
        for (int i = 0; i < n; i++) {
            int pivotRow = i;
            double pivotValue = Math.abs(augmented.get(i, i));
            
            for (int j = i + 1; j < n; j++) {
                double absValue = Math.abs(augmented.get(j, i));
                if (absValue > pivotValue) {
                    pivotRow = j;
                    pivotValue = absValue;
                }
            }
            
            if (pivotValue < 1e-10) {
                throw new ArithmeticException("Matrix is singular and can't be inverted");
            }
            
            if (pivotRow != i) {
                for (int j = 0; j < 2*n; j++) {
                    double temp = augmented.get(i, j);
                    augmented.set(i, j, augmented.get(pivotRow, j));
                    augmented.set(pivotRow, j, temp);
                }
            }
            
            double pivot = augmented.get(i, i);
            for (int j = 0; j < 2*n; j++) {
                augmented.set(i, j, augmented.get(i, j) / pivot);
            }
            
            for (int j = 0; j < n; j++) {
                if (j != i) {
                    double factor = augmented.get(j, i);
                    for (int k = 0; k < 2*n; k++) {
                        augmented.set(j, k, augmented.get(j, k) - factor*augmented.get(i, k));
                    }
                }
            }
        }
        
        RealMatrix inverse = new RealMatrix(n, n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inverse.set(i, j, augmented.get(i, j + n));
            }
        }
        
        return inverse;
    }

    // get data as 2d array
    public double[][] toArray() {
        return data;
    }

    // get specific element
    public double get(int row, int col) {
        return data[row][col];
    }

    // set specific element
    public void set(int row, int col, double value) {
        data[row][col] = value;
    }

    // get number of rows
    public int getRowCount() {
        return rows;
    }

    // get number of columns
    public int getColCount() {
        return cols;
    }

    // set a submatrix
    public void setSubMatrix(RealMatrix subMatrix, int startRow, int startCol) {
        int subRows = subMatrix.getRowCount();
        int subCols = subMatrix.getColCount();
        
        for (int i = 0; i < subRows; i++) {
            for (int j = 0; j < subCols; j++) {
                if (startRow + i < rows && startCol + j < cols) {
                    this.data[startRow + i][startCol + j] = subMatrix.get(i, j);
                }
            }
        }
    }

    // turn it into a string (just in case we need for UI)
    @Override
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