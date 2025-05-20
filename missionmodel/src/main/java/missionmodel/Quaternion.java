package missionmodel;

public class Quaternion {
    public double x, y, z, w; // VECTOR PART FIRST. YOU HAVE BEEN WARNED

    // constructor
    public Quaternion(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    // create identity quaternion
    public static Quaternion identity() {
        return new Quaternion(0, 0, 0, 1);
    }

    // normalize a given quaternion
    public Quaternion normalize() {
        double norm = Math.sqrt(x*x + y*y + z*z + w*w);
        if (norm == 0) throw new ArithmeticException("Cannot normalize zero quaternion.");
        x /= norm;
        y /= norm;
        z /= norm;
        w /= norm;
        return new Quaternion(x, y, z, w);
    }

    // conjugate given quaternion
    public Quaternion conjugate() {
        return new Quaternion(-x, -y, -z, w);
    }

    // invert given quaternion
    public Quaternion inverse() {
        double normSq = x*x + y*y + z*z + w*w;
        if (normSq == 0) throw new ArithmeticException("Zero quaternion has no inverse.");
        return new Quaternion(-x/normSq, -y/normSq, -z/normSq, w/normSq);
    }

    // multiply by a scalar
    public Quaternion scale(double s) {
        return new Quaternion(s*x, s*y, s*z, s*w);
    }

    // quaternion multiplication (this*other)
    public Quaternion multiply(Quaternion other) {
        double nx = w*other.x + x*other.w + y*other.z - z*other.y;
        double ny = w*other.y - x*other.z + y*other.w + z*other.x;
        double nz = w*other.z + x*other.y - y*other.x + z*other.w;
        double nw = w*other.w - x*other.x - y*other.y - z*other.z;
        return new Quaternion(nx, ny, nz, nw);
    }

    // quaternion cross multiplication (that one with the little circle with the x) (this*other)
    public Quaternion cross_multiply(Quaternion other) {
        double nx = w*other.x + x*other.w - (y*other.z - z*other.y);
        double ny = w*other.y + y*other.w - (z*other.x - x*other.z);
        double nz = w*other.z + z*other.w - (x*other.y - y*other.x);
        double nw = w*other.w - (x*other.x + y*other.y + z*other.z);
        return new Quaternion(nx, ny, nz, nw);
    }

    // quaternion dot multiplication (that one with the little circle with the dot) (this*other)
    public Quaternion dot_multiply(Quaternion other) {
        double nx = w*other.x + x*other.w + (y*other.z - z*other.y);
        double ny = w*other.y + y*other.w + (z*other.x - x*other.z);
        double nz = w*other.z + z*other.w + (x*other.y - y*other.x);
        double nw = w*other.w - (x*other.x + y*other.y + z*other.z);
        return new Quaternion(nx, ny, nz, nw);
    }

    // rotate another 3-vector using the quaternion
    public Vector3D rotateVector(Vector3D v) {
        Quaternion vQuat = new Quaternion(v.x, v.y, v.z, 0.0);
        Quaternion rotated = this.multiply(vQuat).multiply(this.inverse());
        return new Vector3D(rotated.x, rotated.y, rotated.z);
    }

    // compute quaternion skew matrix
    public double[][] Xi() {
        double[][] Xi =
        {
            {w, -z, y},
            {z, w, -x},
            {-y, x, w},
            {-x, -y, -z}
        };
    return Xi;
    }

    // convert to Euler angles (yaw-pitch-roll, zyx order)
    public double[] toEulerAngles() {
        double sinr_cosp = 2*(w*x + y*z);
        double cosr_cosp = 1 - 2 * (x*x + y*y);
        double roll = Math.atan2(sinr_cosp, cosr_cosp);

        double sinp = 2 * (w*y - z*x);
        double pitch = Math.abs(sinp) >= 1 ? Math.copySign(Math.PI / 2, sinp) : Math.asin(sinp);

        double siny_cosp = 2*(w*z + x*y);
        double cosy_cosp = 1 - 2*(y*y + z*z);
        double yaw = Math.atan2(siny_cosp, cosy_cosp);

        return new double[] {yaw, pitch, roll};
    }

    // turn the quaternion into equivalent rotation matrix
    public RealMatrix asRotationMatrix() {
        Quaternion q = this.normalize();
        double[][] matrix = new double[3][3];
        
        matrix[0][0] = 1 - 2*q.y*q.y - 2*q.z*q.z;
        matrix[0][1] = 2*q.x*q.y - 2*q.w*q.z;
        matrix[0][2] = 2*q.x*q.z + 2*q.w*q.y;
        matrix[1][0] = 2*q.x*q.y + 2*q.w*q.z;
        matrix[1][1] = 1 - 2*q.x*q.x - 2*q.z*q.z;
        matrix[1][2] = 2*q.y*q.z - 2*q.w*q.x;
        matrix[2][0] = 2*q.x*q.z - 2*q.w*q.y;
        matrix[2][1] = 2*q.y*q.z + 2*q.w*q.x;
        matrix[2][2] = 1 - 2*q.x*q.x - 2*q.y*q.y;
        
        return new RealMatrix(matrix);
    }


    // get scalar part
    public double getScalar() {
        return w;
    }

    // get vector part
    public Vector3D getVector() {
        return new Vector3D(x, y, z);
    }

    // turn into a string for printing
    public String toString() {
        return String.format("Quaternion(x=%.4f, y=%.4f, z=%.4f, w=%.4f)", x, y, z, w);
    }
}