package missionmodel;

public class EKFState {
    public Vector3D position;
    public Vector3D velocity;
    public Quaternion attitude;
    public Vector3D gyroBias;
    public RealMatrix covariance;

    public EKFState(Vector3D position, Vector3D velocity, Quaternion attitude, Vector3D gyroBias, RealMatrix covariance) {
        this.position = position;
        this.velocity = velocity;
        this.attitude = attitude;
        this.gyroBias = gyroBias;
        this.covariance = covariance;
    }
}
