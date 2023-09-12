package org.example.mind.codelets.event_cat_learner;

import br.unicamp.cst.representation.idea.Category;
import br.unicamp.cst.representation.idea.Idea;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventCategory implements Category {
    private String propertyName;
    private double[] eventVector;
    double relevance;

    double MIN_ANGLE_DIFF = 0.1;
    double MIN_MAG_DIFF = 0.1;
    double MIN_DIST_DIFF = 1;
//    double MIN_MAG_DIFF = 4;

    public EventCategory(String propertyName, Idea objectTransition, double relevance) {
        this.propertyName = propertyName;
        this.relevance = relevance;
        Idea timeSteps = objectTransition.get("timeSteps");
        this.eventVector = extractEventVector(timeSteps);

    }
    @Override
    public double membership(Idea objectTransition) {
        Idea timeSteps = objectTransition.get("timeSteps");
        // extracts the event vector based on this.property
        double[] initialPropertyState = extractInitialPropertyState(timeSteps);
        double[] finalPropertyState = extractFinalPropertyState(timeSteps);
        double[] predPropertyState = predictPropertyState(initialPropertyState);

        double result = stateDifference(finalPropertyState, predPropertyState);

        if(result<=MIN_DIST_DIFF) {
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
        Idea firstStep = timeSteps.getL().get(0);

        Idea ithPropertyState = firstStep.get(this.propertyName);

        double[] initialPropertyState = new double[ithPropertyState.getL().size()];

        for(int i=0; i<ithPropertyState.getL().size(); i++) {
            double initialDimIValue = (double) ithPropertyState.getL().get(i).getValue();
            initialPropertyState[i] = initialDimIValue;
        }

        return initialPropertyState;
    }

    private double[] extractFinalPropertyState(Idea timeSteps) {
        int nSteps = timeSteps.getL().size();
        Idea lastStep = timeSteps.getL().get(nSteps-1);

        Idea ithPropertyState = lastStep.get(this.propertyName);

        double[] finalPropertyState = new double[ithPropertyState.getL().size()];

        for(int i=0; i<ithPropertyState.getL().size(); i++) {
            double finalDimIthValue = (double) ithPropertyState.getL().get(i).getValue();
            finalPropertyState[i] = finalDimIthValue;
        }

        return finalPropertyState;
    }

    private double[] extractEventVector(Idea timeSteps) {
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

    boolean sameCategory(EventCategory compCat) {
        if(this.propertyName != compCat.propertyName) {
            return false;
        }

        RealVector catVector = new ArrayRealVector(this.eventVector);
        RealVector compVector = new ArrayRealVector(compCat.eventVector);

        if(isZeroMagnitude(compVector) && isZeroMagnitude(this.eventVector)) {
            return true;
        }

        double magDiff = getMagnitudeDiff(compVector, catVector);
        double angleDiff = getAngleDiff(compVector, catVector);

        if(magDiff<=MIN_MAG_DIFF && angleDiff<=MIN_ANGLE_DIFF) {
            return true;
        }

        return false;
    }

    public boolean hasSimilarAngle(double[] rawVector2) {
        // extracts the event vector based on this.property
        RealVector eventVector1 = new ArrayRealVector(this.eventVector);
        RealVector eventVector2 = new ArrayRealVector(rawVector2);

        if(isZeroMagnitude(eventVector1) && isZeroMagnitude(eventVector2)) {
            return true;
        }

        double angleDiff = getAngleDiff(eventVector1, eventVector2);

        if(angleDiff<=MIN_ANGLE_DIFF) {
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

    public String getPropertyName() {
        return propertyName;
    }

    public double getRelevance() {
        return relevance;
    }

    public void decrementRelevance(double discount) {
        this.relevance *= discount;
    }

    public void incrementRelevance(double increment) {
        this.relevance *= increment;
    }

}
