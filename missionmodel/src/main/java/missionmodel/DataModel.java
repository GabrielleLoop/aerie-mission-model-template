package missionmodel;

import gov.nasa.jpl.aerie.contrib.streamline.core.MutableResource;
import gov.nasa.jpl.aerie.contrib.streamline.modeling.Registrar;
import gov.nasa.jpl.aerie.contrib.streamline.modeling.discrete.Discrete;
import gov.nasa.jpl.aerie.contrib.serialization.mappers.DoubleValueMapper;

import static gov.nasa.jpl.aerie.contrib.streamline.core.MutableResource.resource;
import static gov.nasa.jpl.aerie.contrib.streamline.modeling.discrete.Discrete.discrete;

public class DataModel {

    public MutableResource<Discrete<Vector3D>> AngularMomentum;
    public MutableResource<Discrete<Vector3D>> Position;
    public MutableResource<Discrete<Vector3D>> Velocity;
    public MutableResource<Discrete<Quaternion>> Attitude;
    public MutableResource<Discrete<Quaternion>> DesiredAttitude;
    public MutableResource<Discrete<Vector3D>> AngularVelocity;
    public MutableResource<Discrete<Vector3D>> AngularAcceleration;
    public MutableResource<Discrete<Vector3D>> SurfaceCoordinates;
    public MutableResource<Discrete<Double>> PointingAccuracy;
    public MutableResource<Discrete<Vector3D>> SunPosition;
    public MutableResource<Discrete<RealMatrix>> InertiaMatrix;
    public MutableResource<Discrete<Vector3D>> GyroBias;
    public MutableResource<Discrete<RealMatrix>> Covariance;

    public DataModel(Registrar registrar) {
        AngularMomentum = resource(discrete(Vector3D.zero()));
        Position = resource(discrete(Vector3D.zero()));
        Velocity = resource(discrete(Vector3D.zero()));
        Attitude = resource(discrete(Quaternion.identity()));
        DesiredAttitude = resource(discrete(Quaternion.identity()));
        AngularVelocity = resource(discrete(Vector3D.zero()));
        AngularAcceleration = resource(discrete(Vector3D.zero()));
        SurfaceCoordinates = resource(discrete(Vector3D.zero()));
        PointingAccuracy = resource(discrete(0.0));
        SunPosition = resource(discrete(Vector3D.zero()));
        InertiaMatrix = resource(discrete(RealMatrix.identity(3)));
        GyroBias = resource(discrete(Vector3D.zero()));
        Covariance = resource(discrete(RealMatrix.identity(3)));

        registrar.discrete("AngularMomentum", AngularMomentum, new Vector3DMapper());
        registrar.discrete("Position", Position, new Vector3DMapper());
        registrar.discrete("Velocity", Velocity, new Vector3DMapper());
        registrar.discrete("Attitude", Attitude, new QuaternionMapper());
        registrar.discrete("DesiredAttitude", DesiredAttitude, new QuaternionMapper());
        registrar.discrete("AngularVelocity", AngularVelocity, new Vector3DMapper());
        registrar.discrete("AngularAcceleration", AngularAcceleration, new Vector3DMapper());
        registrar.discrete("SurfaceCoordinates", SurfaceCoordinates, new Vector3DMapper());
        registrar.discrete("PointingAccuracy", PointingAccuracy, new DoubleValueMapper());
        registrar.discrete("SunPosition", SunPosition, new Vector3DMapper());
        registrar.discrete("InertiaMatrix", InertiaMatrix, new RealMatrixMapper());
        registrar.discrete("GyroBias", GyroBias, new Vector3DMapper());
        registrar.discrete("Covariance", Covariance, new RealMatrixMapper());
    }
}
