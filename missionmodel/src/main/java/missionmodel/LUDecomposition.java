package missionmodel;

public class LUDecomposition {
    private final RealMatrix lu;
    private final int[] pivot;
    private final int m;
    private final int n;
    private boolean singular;
    
    public LUDecomposition(RealMatrix matrix) {
        m = matrix.getRowCount();
        n = matrix.getColCount();
        
        // make a copy
        lu = new RealMatrix(m, n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                lu.set(i, j, matrix.get(i, j));
            }
        }
        
        pivot = new int[m];
        for (int i = 0; i < m; i++) {
            pivot[i] = i;
        }
        
        singular = false;
        
        // partial pivot LU decomposition (ugh why do I have to write this whole thing I am tired)
        for (int k = 0; k < Math.min(m, n); k++) {
            // find pivot
            int p = k;
            for (int i = k + 1; i < m; i++) {
                if (Math.abs(lu.get(i, k)) > Math.abs(lu.get(p, k))) {
                    p = i;
                }
            }
            
            // exchange if necessary
            if (p != k) {
                for (int j = 0; j < n; j++) {
                    double t = lu.get(p, j);
                    lu.set(p, j, lu.get(k, j));
                    lu.set(k, j, t);
                }
                int t = pivot[p];
                pivot[p] = pivot[k];
                pivot[k] = t;
            }
            
            // check for singularity
            if (Math.abs(lu.get(k, k)) < 1e-10) {
                singular = true;
                continue;
            }
            
            // compute multipliers, eliminate k-th column
            for (int i = k + 1; i < m; i++) {
                lu.set(i, k, lu.get(i, k) / lu.get(k, k));
                for (int j = k + 1; j < n; j++) {
                    lu.set(i, j, lu.get(i, j) - lu.get(i, k) * lu.get(k, j));
                }
            }
        }
    }
    
    // make and return a solver
    public Solver getSolver() {
        return new Solver();
    }
    
    // solve linear systems
    public class Solver {
        
        // solve AX = B
        public double[] solve(double[] b) {
            if (singular) {
                throw new ArithmeticException("Matrix is singular");
            }
            
            if (b.length != m) {
                throw new IllegalArgumentException("Invalid vector dimension");
            }
            
            // permute right hand side
            double[] bp = new double[m];
            for (int i = 0; i < m; i++) {
                bp[i] = b[pivot[i]];
            }
            
            // solve L*Y = B(piv)
            double[] y = new double[m];
            for (int i = 0; i < m; i++) {
                y[i] = bp[i];
                for (int j = 0; j < i; j++) {
                    y[i] -= lu.get(i, j) * y[j];
                }
            }
            
            // solve U*X = Y
            double[] x = new double[n];
            for (int i = Math.min(m, n) - 1; i >= 0; i--) {
                x[i] = y[i];
                for (int j = i + 1; j < n; j++) {
                    x[i] -= lu.get(i, j) * x[j];
                }
                x[i] /= lu.get(i, i);
            }
            
            return x;
        }
        
        // can't remember if I already wrote this one but it gets the inverse (just remove if it's a duplicate)
        public RealMatrix getInverse() {
            if (singular) {
                throw new ArithmeticException("Matrix is singular");
            }
            
            if (m != n) {
                throw new ArithmeticException("Matrix is not square");
            }
            
            RealMatrix inverse = new RealMatrix(n, n);
            
            for (int j = 0; j < n; j++) {
                double[] b = new double[n];
                b[j] = 1.0;
                
                double[] x = solve(b);
                
                for (int i = 0; i < n; i++) {
                    inverse.set(i, j, x[i]);
                }
            }
            
            return inverse;
        }
    }
}