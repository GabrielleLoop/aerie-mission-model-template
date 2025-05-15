package missionmodel;

import gov.nasa.jpl.aerie.contrib.streamline.modeling.discrete.DiscreteEffects;
import gov.nasa.jpl.aerie.merlin.framework.annotations.ActivityType;
import gov.nasa.jpl.aerie.merlin.framework.annotations.Export.Parameter;
import gov.nasa.jpl.aerie.merlin.framework.annotations.Export.Validation;


import gov.nasa.jpl.aerie.merlin.protocol.types.Duration;

import static gov.nasa.jpl.aerie.merlin.framework.ModelActions.delay;
import static gov.nasa.jpl.aerie.merlin.protocol.types.Duration.SECONDS;

@ActivityType("Pointing")
public class Pointing {

    @Parameter
    public ${Data Type} ${Parameter 1} = ${Value}; // ${Units... if applicable}

    @Parameter
    public ${Data Type} ${Parameter 2} = ${Value}; // ${Units... if applicable}




    /**

        [ SECTION B : VALIDATION ]

    @Validation(" ${Error message} ")
    @Validation.Subject(" ${Parameter 1} ")
    public boolean ${Method Name (usually something like validationParameterName)}() {
        return ${Parameter 1} ...               Some sort of way to quantify success (less than, less than equal to, etc)
    }
    
    
     */




        /**
        
            [ SECTION (if including ComputedAttributes) ]

        @AutoValueMapper.Record
        record ComputedAttributes (
            ${Data Type} ${Variable}
            ${Data Type} ${Variable}
        )
        
        
        */



    @ActivityType.EffectModel
    // (if applicable, add Duration here for Controlled or Parameterized Duration)
    public void run(Mission model) { // if including ComputedAttributes, change to [ public ComputedAttributes run(Mission mission) ]


        /**
        


            [ SECTION C: ACTUAL EFFECT ON RESOURCES ]


            DISCRETEEFFECTS:
            Boolean Resource Methods:
            - DiscreteEffects.turnOn(...) – Set a Boolean resource to true.
            - DiscreteEffects.turnOff(...) – Set a Boolean resource to false.
            - DiscreteEffects.toggle(...) – Flip a Boolean resource between true and false.
            Integer Resource Methods:
            - DiscreteEffects.increment(...) – Increase an Integer resource by 1.
            - DiscreteEffects.increment(..., int amount) – Increase an Integer resource by a specific amount.
            - DiscreteEffects.decrement(...) – Decrease an Integer resource by 1.
            - DiscreteEffects.decrement(..., int amount) – Decrease an Integer resource by a specific amount.
            - DiscreteEffects.using(..., Runnable action) – Temporarily decrement an Integer resource by 1 while an action runs.
            Double Resource Methods:
            - DiscreteEffects.increase(...) – Add a specific amount to a Double resource.
            - DiscreteEffects.decrease(...) – Subtract a specific amount from a Double resource.
            - DiscreteEffects.consume(...) – Subtract a specific amount from a Double resource (same as decrease).
            - DiscreteEffects.restore(...) – Add a specific amount to a Double resource (same as increase).
            - DiscreteEffects.using(..., double amount, Runnable) – Temporarily decrease a Double resource by a given amount during an action.
            List Resource Methods:
            - DiscreteEffects.add(...) – Append an element to a List resource.
            - DiscreteEffects.remove(...) – Remove and return the first element from a List resource (if present)
            General Method for all types:
            - DiscreteEffects.set(...) – Set a resource to a specific value (works with any type).
        

            TIME RELATED METHODS: (static methods from ModelActions class)
            - delay(duration) - delay the currently running activity for the given duration
            - waitUntil(condition) - delay currently running activity until condition becomes true
                - for both... they will observe effects caused by other activities over the intervening timespan on resumption
            

            ACTION RELATED METHODS: (provided by ActivityActions class)
            - spawn(mission, activity) - spawn a new activity as child of current activity at the current point in time and the
                    parent activity will continue uninterrupted
            - call(mission, activity) - spawn a new activity as child of current activity at the current point in time and the
                    parent activity will halt until child activity is completed
            




         */




        /**
        
            If including ComputedAttributes/logging output...

        return new ComputedAttributes(${Variable 1}, ${Variable 2})
        
        
         */



    }
}



// HUZZAH YOU'RE DONE


