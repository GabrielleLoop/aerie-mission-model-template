package missionmodel;

public class Vector3D {
    public double x, y, z;

    // constructor
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // zero vector (bartender, make mine a double)
    public static Vector3D zero() {
        return new Vector3D(0.0, 0.0, 0.0);
    }

    // add another vector to this one
    public Vector3D add(Vector3D v) {
        return new Vector3D(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    // subtract another vector from this one
    public Vector3D subtract(Vector3D v) {
        return new Vector3D(this.x - v.x, this.y - v.y, this.z - v.z);
    }

    // multiply by a scalar
    public Vector3D scale(double s) {
        return new Vector3D(s*this.x, s*this.y, s*this.z);
    }

    // dot product
    public double dot(Vector3D v) {
        return this.x*v.x + this.y*v.y + this.z*v.z;
    }

    // cross product (this x other)
    public Vector3D cross(Vector3D v) {
        return new Vector3D(
            this.y*v.z - this.z*v.y,
            this.z*v.x - this.x*v.z,
            this.x*v.y - this.y*v.x
        );
    }

    // find the norm of this vector
    public double norm() {
        return Math.sqrt(this.x*this.x + this.y*this.y + this.z*this.z);
    }

    // normalize vector
    public Vector3D normalize() {
        double n = this.norm();
        if (n == 0.0) throw new ArithmeticException("Cannot normalize zero vector.");
        return this.scale(1.0/n);
    }

    // convert to array
    public double[] toArray() {
        return new double[] {x, y, z};
    }

    // skew
    public double[][] skew() {
        return new double[][] {
            {0.0, -z, y},
            {z, 0.0, -x},
            {-y, x, 0.0}
        };
    }

    // override toString so it prints nicely
    public String toString() {
        return String.format("Vector3D(%.4f, %.4f, %.4f)", x, y, z);
    }
}

