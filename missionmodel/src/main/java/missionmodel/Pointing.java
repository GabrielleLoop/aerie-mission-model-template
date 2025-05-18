package missionmodel;

import gov.nasa.jpl.aerie.contrib.streamline.modeling.discrete.DiscreteEffects;
import gov.nasa.jpl.aerie.merlin.framework.annotations.ActivityType;
import gov.nasa.jpl.aerie.merlin.framework.annotations.Export.Parameter;
import gov.nasa.jpl.aerie.merlin.framework.annotations.Export.Validation;
import gov.nasa.jpl.aerie.merlin.protocol.types.Duration;

import static gov.nasa.jpl.aerie.merlin.framework.ModelActions.delay;
import static gov.nasa.jpl.aerie.merlin.protocol.types.Duration.SECONDS;

@ActivityType("Pointing")
public final class Pointing {

    // --- SECTION A: PARAMETERS ---
    // proportional gain
    @Parameter
    public double kp = 0.1; // Nm/rad

    // derivative gain
    @Parameter
    public double kd = 0.05; // Nm/rad/s

    // time for maneuver
    @Parameter
    public double t_f; // s

    // --- SECTION B: VALIDATION ---

    @Validation("Proportional gain must be non-negative.")
    @Validation.Subject("kp")
    public boolean validateKp() {
        return kp >= 0;
    }

    @Validation("Derivative gain must be non-negative.")
    @Validation.Subject("kd")
    public boolean validateKd() {
        return kd >= 0;
    }

    @Validation("Final time must be positive.")
    @Validation.Subject("t_f")
    public boolean validateTf() {
        return t_f > 0;
    }

    // --- SECTION C: EFFECT MODEL ---

    @ActivityType.EffectModel
    // I GENUINELY HAVE NO IDEA HOW TO DO THIS DURATION SINCE IT'S CALCULATED INSIDE THE ACTIVITY???? GABBY HLP
    public void run(Mission model) {
        // retrieve initial state from the model
        Quaternion q_0 = model.Attitude.get();
        Quaternion q_des = model.DesiredAttitude.get();
        Vector3D omega_0 = model.AngularVelocity.get();
        RealMatrix I_c = model.InertiaMatrix;

        // compute delta quaternion (difference between desired and current attitude)
        Quaternion q = q_0;
        Vector3D omega = omega_0;
        Quaternion del_q = computeDeltaQ(q, q_des);

        // time span for integration
        // uhhhh let's assume bang-bang control for now so tf = 4*theta*I/Lmax
        double angle = 2*Math.acos(del_q.getScalar());
        t_f = angle*18.7; // if specs of the satellite change, this needs to be recalculated
        int steps = 200;
        double dt = t_f/steps;

        // integration (idk exactly how this is done in Aerie so change it however you need gabby but this is what goes in the integral)
        for (int i = 0; i < steps; i++) {
            // control torque
            Vector3D torque = computeControlTorque(del_q, omega, I_c);

            // take derivatives
            Vector3D omega_dot = I_c.invert().multiply(torque.subtract(omega.cross(I_c.multiply(omega))));
            Quaternion q_dot = computeQDot(q, omega);
            Quaternion del_q_dot = computeDeltaQ(q_dot, q_des);

            // INTEGRATE MEEEEEEEEEE, omega_dot, q_dot, del_q_dot need to be updated to become the next omega, q, and del_q
        }

        // update model resources with final state
        DiscreteEffects.set(model.Attitude, q);
        DiscreteEffects.set(model.DesiredAttitude, new Quaternion(0, 0, 0, 1));
        DiscreteEffects.set(model.AngularVelocity, omega);
    }

    // fonctions

    private Quaternion computeDeltaQ(Quaternion q, Quaternion q_des) {
        return q_des.cross_multiply(q.conjugate()).normalize();
    }

    private Vector3D computeControlTorque(Quaternion del_q, Vector3D omega, RealMatrix I_c) {
        Vector3D del_q_vec = new Vector3D(del_q.x, del_q.y, del_q.z);
        Vector3D proportional = del_q_vec.scale(-kp);
        Vector3D derivative = omega.scale(-kd);
        Vector3D cross = omega.cross(I_c.multiply(omega));
        return I_c.multiply(proportional.add(derivative)).add(cross);
    }

    private Quaternion computeQDot(Quaternion q, Vector3D omega) {
        double[][] Xi = q.Xi();
        double nx = 0.5*(Xi[0][0]*omega.x + Xi[0][1]*omega.y + Xi[0][2]*omega.z);
        double ny = 0.5*(Xi[1][0]*omega.x + Xi[1][1]*omega.y + Xi[1][2]*omega.z);
        double nz = 0.5*(Xi[2][0]*omega.x + Xi[2][1]*omega.y + Xi[2][2]*omega.z);
        double nw = 0.5*(Xi[3][0]*omega.x + Xi[3][0]*omega.y + Xi[3][0]*omega.z);
        return new Quaternion(nx, ny, nz, nw);
    }
}
