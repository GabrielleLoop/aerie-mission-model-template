package missionmodel;
import missionmodel.Vector3D;
import missionmodel.Quaternion;

public class AttitudeTargeting {

    // enforce quaternion use
    public static Quaternion deltaVToQuaternion(Vector3D deltaV) {

        // normalize delta V
        Vector3D v1 = deltaV.normalize();

        // body z-axis
        Vector3D v2 = new Vector3D(0, 0, 1);

        // compute rotation axis
        Vector3D axis = v1.cross(v2);
        double axisNorm = axis.norm();

        // handle edge cases
        if (axisNorm == 0) {
            double dot = v1.dot(v2);
            if (dot > 0) {
                return new Quaternion(0, 0, 0, 1); // no rotation
            } else {
                return new Quaternion(1, 0, 0, 0); // 180-degree rotation in x
            }
        } else {
            axis.scale(1.0/axisNorm);
        }

        // compute angle
        double theta = Math.acos(v1.dot(v2));
        double[] axisEls = axis.toArray();

        // compute quaternion
        return new Quaternion(
            axisEls[0]*Math.sin(theta/2),
            axisEls[1]*Math.sin(theta/2),
            axisEls[2]*Math.sin(theta/2),
            Math.cos(theta/2)
        );
    }
}
