package missionmodel;

import gov.nasa.jpl.aerie.contrib.streamline.modeling.discrete.DiscreteEffects;
import gov.nasa.jpl.aerie.merlin.framework.annotations.ActivityType;
import gov.nasa.jpl.aerie.merlin.framework.annotations.Export.Parameter;
import gov.nasa.jpl.aerie.merlin.framework.annotations.Export.Validation;
import gov.nasa.jpl.aerie.merlin.protocol.types.Duration;

import static gov.nasa.jpl.aerie.merlin.framework.ModelActions.delay;
import static java.lang.Math;

@ActivityType("AttitudeTargeting")
public class AttitudeTargeting {

    // --- SECTION A: PARAMETERS ---
    @Parameter
    public Vector3D deltaV = new Vector3D(0.0, 0.0, 1.0); // m/s

    // --- SECTION C: EFFECT MODEL ---
    @ActivityType.EffectModel
    public void run(Mission model) {

        // normalize delta-v vector (v1)
        Vector3D v1 = deltaV.normalize();

        // v2 is body z-axis in body frame
        Vector3D v2 = new Vector3D(0.0, 0.0, 1.0);

        // cross product: axis = v1 × v2
        Vector3D axis = v1.cross(v2);
        double axisNorm = axis.norm();

        double qx, qy, qz, qw;

        if (axisNorm < 1e-6) {
            // Handle edge case
            double dot = v1x * v2x + v1y * v2y + v1z * v2z;
            if (dot > 0) {
                qx = qy = qz = 0.0;
                qw = 1.0;
            } else {
                qx = 1.0; qy = 0.0; qz = 0.0; qw = 0.0; // 180-deg around x-axis
            }
        } else {
            // Normalize axis
            axisX /= axisNorm;
            axisY /= axisNorm;
            axisZ /= axisNorm;

            // Compute angle
            double dot = v1x * v2x + v1y * v2y + v1z * v2z;
            double theta = acos(dot);

            // Quaternion: q = [axis * sin(θ/2), cos(θ/2)]
            double halfTheta = theta / 2.0;
            double sinHalfTheta = sin(halfTheta);
            qx = axisX * sinHalfTheta;
            qy = axisY * sinHalfTheta;
            qz = axisZ * sinHalfTheta;
            qw = cos(halfTheta);
        }

        // Output for debug (or use in effect model)
        System.out.printf("Quaternion: [%.4f, %.4f, %.4f, %.4f]%n", qx, qy, qz, qw);
        
        // Here you might set this quaternion into a model state or log it
        // DiscreteEffects.set(model.someAttitudeResource, new Quaternion(qx, qy, qz, qw));
    }
}
