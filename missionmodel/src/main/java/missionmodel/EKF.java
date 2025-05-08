package missionmodel;

public class EKF {
    private EKFState state;
    private RealMatrix Q; // process noise (13x13)
    private RealMatrix R; // measurement noise (6x6)

    public EKF(EKFState initialState, RealMatrix processNoise, RealMatrix measurementNoise) {
        this.state = initialState;
        this.Q = processNoise;
        this.R = measurementNoise;
    }

    public void propagate(Vector3D accMeas, Vector3D gyroMeas, double dt) {
        // angular velocity, with bias corrected 
        Vector3D omega = gyroMeas.subtract(state.gyroBias);
        
        // quat propagation
        Quaternion dq = QuaternionUtils.integrateOmega(state.attitude, omega, dt);
        state.attitude = dq.normalize();

        // velocity and position propagation
        Vector3D gravity = new Vector3D(0, 0, -1.625);
        Vector3D accWorld = state.attitude.rotate(accMeas);
        state.velocity = state.velocity.add(accWorld.add(gravity).scalarMultiply(dt));
        state.position = state.position.add(state.velocity.scalarMultiply(dt));

        // FIXMEEEEE: Construct F matrix (13x13), propagate covariance
    }

    public void update(Vector3D posMeas, Quaternion attMeas) {
        // residual (measurements)
        Vector3D posResidual = posMeas.subtract(state.position);
        Quaternion qError = attMeas.multiply(state.attitude.inverse());
        Vector3D deltaTheta = QuaternionUtils.smallAngleApprox(qError);

        // measurement model jacobian H (6x13)
        RealMatrix H = MatrixUtils.createRealMatrix(6, 13);
        H.setSubMatrix(MatrixUtils.createRealIdentityMatrix(3).getData(), 0, 0); // Position
        H.setSubMatrix(MatrixUtils.createRealIdentityMatrix(3).getData(), 3, 6); // Attitude error

        // kalman gain
        RealMatrix S = H.multiply(state.covariance).multiply(H.transpose()).add(R);
        RealMatrix K = state.covariance.multiply(H.transpose()).multiply(new LUDecomposition(S).getSolver().getInverse());

        // correct using kalman gain
        RealVector innovation = new ArrayRealVector(6);
        innovation.setSubVector(0, posResidual);
        innovation.setSubVector(3, deltaTheta);
        RealVector deltaX = K.operate(innovation);

        // apply to state
        state.position = state.position.add(deltaX.getSubVector(0, 3));
        state.velocity = state.velocity.add(deltaX.getSubVector(3, 3));
        Quaternion dQuat = QuaternionUtils.deltaThetaToQuat(deltaX.getSubVector(6, 3));
        state.attitude = dQuat.multiply(state.attitude).normalize();
        state.gyroBias = state.gyroBias.add(deltaX.getSubVector(9, 3));

        // update covariance
        RealMatrix I = MatrixUtils.createRealIdentityMatrix(13);
        state.covariance = I.subtract(K.multiply(H)).multiply(state.covariance);
    }

    public EKFState getState() {
        return state;
    }
}