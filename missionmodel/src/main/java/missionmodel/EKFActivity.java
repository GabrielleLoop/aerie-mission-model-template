package missionmodel;

import gov.nasa.jpl.aerie.contrib.streamline.modeling.discrete.DiscreteEffects;
import gov.nasa.jpl.aerie.merlin.framework.annotations.ActivityType;
import gov.nasa.jpl.aerie.merlin.framework.annotations.Export.Parameter;
import gov.nasa.jpl.aerie.merlin.protocol.types.Duration;
import static gov.nasa.jpl.aerie.merlin.framework.ModelActions.delay;

@ActivityType("EKFActivity")
public class EKFActivity {
    
    @Parameter 
    public RealMatrix Q; // process noise

    @Parameter 
    public RealMatrix R; // measurement noise

    // need to be read in from imu sim
    @Parameter 
    public Vector3D accMeas;

    @Parameter 
    public Vector3D gyroMeas;

    @Parameter 
    public Vector3D posMeas;

    @Parameter 
    public Quaternion attMeas;

    @Parameter public double dt; // s

    @ActivityType.EffectModel
    public void run(Mission model) {
        // assign resource variables
        Vector3D position = model.Position.get();
        Vector3D velocity = model.Velocity.get();
        Quaternion attitude = model.Attitude.get();
        Vector3D gyroBias = model.GyroBias.get();
        Vector3D covariance = model.Covariance.get();

        // propagate
        Vector3D omega = gyroMeas.subtract(gyroBias);
        Quaternion dq = integrateOmega(attitude, omega, dt);
        attitude = dq.normalize();

        Vector3D gravity = new Vector3D(0, 0, -1.625);
        Vector3D accWorld = attitude.rotateVector(accMeas);
        velocity = velocity.add(accWorld.add(gravity).scale(dt));
        position = position.add(velocity.scale(dt));

        // build F and phi
        RealMatrix F = new RealMatrix(13, 13);
        F.setSubMatrix(RealMatrix.identity(3).scale(1.0), 0, 3);

        RealMatrix Rwb = attitude.asRotationMatrix();
        RealMatrix accSkew = skewSymmetric(accMeas);
        F.setSubMatrix(Rwb.multiply(accSkew).scale(-1.0), 3, 6);
        F.setSubMatrix(RealMatrix.identity(3).scale(-1.0),6, 9);

        RealMatrix I = RealMatrix.identity(13);
        RealMatrix Phi = I.add(F.scale(dt));

        covariance = Phi.multiply(covariance).multiply(Phi.transpose()).add(Q);

        Vector3D posResidual = posMeas.subtract(position);
        Quaternion qError = attMeas.multiply(attitude.inverse());
        Vector3D deltaTheta = smallAngleApprox(qError);

        RealMatrix H = new RealMatrix(6, 13);
        H.setSubMatrix(RealMatrix.identity(3), 0, 0);
        H.setSubMatrix(RealMatrix.identity(3), 3, 6);

        RealMatrix S = H.multiply(covariance).multiply(H.transpose()).add(R);
        RealMatrix K = covariance.multiply(H.transpose()).multiply(new LUDecomposition(S).getSolver().getInverse());

        double[] innovation = {posResidual.x, posResidual.y, posResidual.z, deltaTheta.x, deltaTheta.y, deltaTheta.z};

        RealMatrix innovationMatrix = new RealMatrix(6, 1);
        for (int i = 0; i < 6; i++) {
            innovationMatrix.set(i, 0, innovation[i]);
        }
        RealMatrix deltaXMatrix = K.multiply(innovationMatrix);

        position = position.add(new Vector3D(
            deltaXMatrix.get(0, 0),
            deltaXMatrix.get(1, 0),
            deltaXMatrix.get(2, 0)
        ));

        velocity = velocity.add(new Vector3D(
            deltaXMatrix.get(3, 0),
            deltaXMatrix.get(4, 0),
            deltaXMatrix.get(5, 0)
        ));

        Quaternion dQuat = deltaThetaToQuat(new Vector3D(
            deltaXMatrix.get(6, 0),
            deltaXMatrix.get(7, 0),
            deltaXMatrix.get(8, 0)
        ));
        attitude = dQuat.multiply(attitude).normalize();

        gyroBias = gyroBias.add(new Vector3D(
            deltaXMatrix.get(9, 0),
            deltaXMatrix.get(10, 0),
            deltaXMatrix.get(11, 0)
        ));

        covariance = I.subtract(K.multiply(H)).multiply(covariance);

        DiscreteEffects.set(model.Position, position);
        DiscreteEffects.set(model.Velocity, velocity);
        DiscreteEffects.set(model.Attitude, attitude);
        DiscreteEffects.set(model.GyroBias, gyroBias);
        DiscreteEffects.set(model.Covariance, covariance);
    }

    // functions
    
    public static Quaternion integrateOmega(Quaternion q, Vector3D omega, double dt) {
        double omegaNorm = omega.norm();
        if (omegaNorm == 0) return q;

        double theta = omegaNorm*dt;
        Vector3D axis = omega.normalize();
        Quaternion deltaQ = new Quaternion(
            axis.x*Math.sin(theta/2),
            axis.y*Math.sin(theta/2),
            axis.z*Math.sin(theta/2),
            Math.cos(theta/2)
        );
        return deltaQ.multiply(q).normalize();
    }

    public static Vector3D smallAngleApprox(Quaternion q) {
        // assume q is close to identity
        return new Vector3D(2*q.x, 2*q.y, 2*q.z);
    }

    public static Quaternion deltaThetaToQuat(Vector3D deltaTheta) {
        double theta = deltaTheta.norm();
        if (theta == 0) return new Quaternion(0, 0, 0, 1);

        Vector3D axis = deltaTheta.normalize();
        return new Quaternion(
            axis.x*Math.sin(theta/2),
            axis.y*Math.sin(theta/2),
            axis.z*Math.sin(theta/2),
            Math.cos(theta/2)
        );
    }

    public static RealMatrix skewSymmetric(Vector3D v) {
        return new RealMatrix(new double[][] {
            {0, -v.z, v.y},
            {v.z, 0, -v.x},
            {-v.y, v.x, 0}
        });
    }
}