package missionmodel;

import gov.nasa.jpl.aerie.contrib.streamline.core.MutableResource;
import gov.nasa.jpl.aerie.contrib.streamline.modeling.Registrar;
import gov.nasa.jpl.aerie.contrib.streamline.modeling.discrete.Discrete;
import gov.nasa.jpl.aerie.contrib.serialization.mappers.DoubleValueMapper;

import java.util.Arrays;
import java.util.List;

import static gov.nasa.jpl.aerie.contrib.streamline.core.MutableResource.resource;
import static gov.nasa.jpl.aerie.contrib.streamline.modeling.discrete.Discrete.discrete;

public class DataModel {

    public MutableResource<Discrete<List<Double>>> AngularMomentum;

    public DataModel(Registrar registrar) {
        AngularMomentum = resource(discrete(Arrays.asList(0.0, 0.0, 0.0)));
        registrar.discrete("AngularMomentum", AngularMomentum, new ListOfDoubleValueMapper());
    }
}
