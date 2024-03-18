package org.example.mind.codelets.event_cat_learner;

import br.unicamp.cst.representation.idea.Idea;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VectorEventCategory implements EventCategory {
    private String propertyName;
    private String objectCategoryName;
    private double[] eventVector;
    double relevance;

    double MIN_ANGLE_DIFF = 0.25 * Math.PI; // 45 degree angle
    double MIN_MAG_DIFF = 5;
    // hypotenuse of the rectangle triangle
    double MIN_DIST_DIFF = Math.sqrt(Math.pow(MIN_MAG_DIFF,2)+Math.pow(MIN_MAG_DIFF,2));


    public VectorEventCategory(String propertyName, Idea objectTransition, double relevance) {
        this.propertyName = propertyName;
        this.relevance = relevance;
        this.eventVector = extractEventVector(objectTransition);
        this.objectCategoryName = getObjectCategoryName(objectTransition);
    }
    @Override
    public double membership(Idea objectTransition) {

        // Checks is the object category matches;
        String objectCategoryName = getObjectCategoryName(objectTransition);
        if(objectCategoryName==objectCategoryName && !this.objectCategoryName.equals(objectCategoryName)) {
            return 0;
        }

        // extracts the event vector based on this.property;
        Idea timeSteps = objectTransition.get("timeSteps");
        double[] initialPropertyState = extractInitialPropertyState(timeSteps);
        double[] finalPropertyState = extractFinalPropertyState(timeSteps);

        if(initialPropertyState==null || finalPropertyState==null) {
            return 0;
        }

        RealVector catVector = new ArrayRealVector(this.eventVector);
        RealVector transitionVector = new ArrayRealVector(getVectorFromInitialAndFinalState(initialPropertyState,
                finalPropertyState));

        double angleDiff = getAngleDiff(transitionVector, catVector);

        double[] predPropertyState = predictPropertyState(initialPropertyState);
        double result = stateDifference(finalPropertyState, predPropertyState);

        // in the case event vector has 0 magnitude, the property should stay absolutely fixed
        if(isZeroMagnitude(eventVector) && result>0) {
            return 0;
        }

        if(isZeroMagnitude(eventVector) && isZeroMagnitude(transitionVector)) {
            return 1;
        }

        //compares the prediction distance and angle
        if(result<MIN_DIST_DIFF && angleDiff<MIN_ANGLE_DIFF) {
            return 1;
        }
        return 0;
    }


    @Override
    public Idea getInstance(List<Idea> constraints) {
        return null;
    }

    private double stateDifference(double[] state1, double[] state2) {

        if(state1.length!= state2.length) {
            System.out.println("States with different dimensions");
        }

        double powerSum = 0;

        for(int i=0; i<state1.length; i++) {
            powerSum = powerSum + Math.pow((state1[i]-state2[i]), 2);
        }

        double result = Math.sqrt(powerSum);

        return result;
    }

    private double[] predictPropertyState(double[] initialState) {
        double[] predState = new double[initialState.length];

        for(int i=0; i<initialState.length; i++) {
            predState[i] = initialState[i] + this.eventVector[i];
        }
        return predState;
    }

    private double[] extractInitialPropertyState(Idea timeSteps) {
        Idea firstObjectStep = timeSteps.getL().get(0).get("idObject");
        if(firstObjectStep == null) {
            return null;
        }

        Idea ithPropertyState = firstObjectStep.get(this.propertyName);

        double[] initialPropertyState = new double[ithPropertyState.getL().size()];

        for(int i=0; i<ithPropertyState.getL().size(); i++) {
            double initialDimIValue = (double) ithPropertyState.getL().get(i).getValue();
            initialPropertyState[i] = initialDimIValue;
        }

        return initialPropertyState;
    }


    private double[] extractFinalPropertyState(Idea timeSteps) {
        int nSteps = timeSteps.getL().size();
        Idea lastObjectStep = timeSteps.getL().get(nSteps-1).get("idObject");

        if(lastObjectStep==null) {
            return null;
        }

        Idea ithPropertyState = lastObjectStep.get(this.propertyName);

        double[] finalPropertyState = new double[ithPropertyState.getL().size()];

        for(int i=0; i<ithPropertyState.getL().size(); i++) {
            double finalDimIthValue = (double) ithPropertyState.getL().get(i).getValue();
            finalPropertyState[i] = finalDimIthValue;
        }

        return finalPropertyState;
    }

    private double[] extractEventVector(Idea objectTransition) {
        Idea timeSteps = objectTransition.get("timeSteps");
        double[] initialState = extractInitialPropertyState(timeSteps);
        double[] finalState = extractFinalPropertyState(timeSteps);

        if(initialState.length!=finalState.length) {
            System.out.println("Property with inconsistent number of quality dimensions");
        }

        double[] eventVector = new double[initialState.length];

        for(int i=0; i<initialState.length; i++) {
            eventVector[i] = finalState[i]-initialState[i];
        }

        return eventVector;
    }

    @Override
    public boolean sameCategory(EventCategory compCat) {
        // Checks if the same type of event category;
        if(!(compCat instanceof VectorEventCategory)) {
            return false;
        }

        VectorEventCategory vecCompCat = (VectorEventCategory) compCat;

        if(!this.objectCategoryName.equals(vecCompCat.getObjectCategoryName())) {
            return false;
        }

        if(!this.propertyName.equals(vecCompCat.getPropertyName())) {
            return false;
        }

        RealVector catVector = new ArrayRealVector(this.eventVector);
        RealVector compVector = new ArrayRealVector(vecCompCat.getEventVector());

        if(isZeroMagnitude(compVector) && isZeroMagnitude(this.eventVector)) {
            return true;
        }

        if((isZeroMagnitude(compVector) && !isZeroMagnitude(this.eventVector))
                || (!isZeroMagnitude(compVector) && isZeroMagnitude(this.eventVector))) {
            return false;
        }

        double magDiff = getMagnitudeDiff(compVector, catVector);
        double angleDiff = getAngleDiff(compVector, catVector);

        if(magDiff<=MIN_MAG_DIFF && angleDiff<=MIN_ANGLE_DIFF) {
            return true;
        }

        return false;
    }

    private double getMagnitudeDiff(RealVector vectorA, RealVector vectorB) {
        double aMag = vectorA.getNorm();
        double bMag = vectorB.getNorm();

        return Math.abs(aMag-bMag);
    }

    private double getAngleDiff(RealVector vectorA, RealVector vectorB) {
        double dotProduct = vectorA.dotProduct(vectorB);

        double aMag = vectorA.getNorm();
        double bMag = vectorB.getNorm();

        double cosAngle = dotProduct/(aMag*bMag);
        cosAngle = Math.max(-1.0, Math.min(cosAngle, 1.0)); // Clamp cosAngle to [-1, 1]

        double angleDiff = Math.acos(cosAngle);

        return Math.abs(angleDiff);
    }


    private boolean isZeroMagnitude(double[] vector) {
        for(int i=0; i<vector.length; i++) {
            if(vector[i]!=0) {
                return false;
            }
        }
        return true;
    }

    private boolean isZeroMagnitude(RealVector vector) {
        if(vector.getNorm()==0) {
            return true;
        }
        return false;
    }

    private String getObjectCategoryName(Idea objectTransition) {
        String categoryName = null;
        for(Idea timeStep: objectTransition.get("timeSteps").getL()) {
            Idea object = timeStep.get("idObject");

            if(object!=null) {
                String currCategoryName = (String) object.get("objectCategory").getValue();

                if(categoryName==null || categoryName==currCategoryName) {
                    categoryName = currCategoryName;
                } else {
                    Logger.getLogger(AppearanceEventCategory.class.getName()).log(Level.SEVERE,
                            "object category changes between time steps.");
                }
            }

        }
        return categoryName;
    }
    private double[] getVectorFromInitialAndFinalState(double[] initialState, double[] finalState) {
        //
        double[] vector = new double[initialState.length];

        for(int i=0; i<initialState.length; i++) {
            vector[i] = finalState[i] - initialState[i];
        }
        return vector;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getObjectCategoryName() {
        return objectCategoryName;
    }

    public double[] getEventVector() {
        return eventVector;
    }

    @Override
    public double getRelevance() {
        return relevance;
    }

    @Override
    public void decrementRelevance(double discount) {
        this.relevance *= discount;
    }
    @Override
    public void incrementRelevance(double increment) {
        this.relevance *= increment;
    }

}
