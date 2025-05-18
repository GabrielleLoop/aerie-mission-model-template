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
    // axis of bodyodyodyodyodyodyody
    @Parameter
    public Vector3D v2 = new Vector3D(0.0, 0.0, 1.0); // m/s

    @FixedDuration
    public static final Duration TOTAL_DURATION = Duration.ofSeconds(0.001); // just gonna set it really small since it shouldn't take any time

    // --- SECTION C: EFFECT MODEL ---
    @ActivityType.EffectModel
    public void run(Mission model) {

        // get delta v vector from resources and norm
        Vector3D deltaV = model.deltaV.get();
        deltaV.normalize();

        // cross product: axis = v1 × v2
        Vector3D axis = deltaV.cross(v2);
        double axisNorm = axis.norm();

        double qx, qy, qz, qw;

        if (axisNorm < 1e-6) {
            // check for parallel
            double dot = deltaV.x*v2.x + deltaV.y*v2.y + deltaV.z*v2.z;
            // if they're already in the same direction, return identity quaternion
            if (dot > 0) {
                qx = qy = qz = 0.0;
                qw = 1.0;
            // if they're in opposite directions, return 180-deg rotation around x-axis
            } else {
                qx = 1.0; qy = 0.0; qz = 0.0; qw = 0.0;
            }
        } else {
            // normalize axis
            axis.normalize();

            // compute angle between vectors
            double dot = deltaV.x*v2.x + deltaV.y*v2.y + deltaV.z*v2.z;
            double theta = Math.acos(dot);

            // form quaternion
            qx = axis.x*Math.sin(theta/2.0);
            qy = axis.y*Math.sin(theta/2.0);
            qz = axis.z*Math.sin(theta/2.0);
            qw = Math.cos(theta/2.0);
        }
        
        // output!
        DiscreteEffects.set(model.DesiredAttitude, new Quaternion(qx, qy, qz, qw));
    }
}
