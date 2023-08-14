package org.example.mind.codelets.event_cat_learner;

import br.unicamp.cst.representation.idea.Category;
import br.unicamp.cst.representation.idea.Idea;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.List;

public class EventCategory implements Category {
    public String propertyName;
    public RealVector eventVector;
    double relevance;

    double MIN_ANGLE_DIFF = 0.01;
    double MIN_MAG_DIFF = 0.01;

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
        RealVector instVector = extractEventVector(timeSteps);

        if(isZeroMagnitude(instVector) && isZeroMagnitude(this.eventVector)) {
            return 1;
        }

        double magDiff = getMagnitudeDiff(instVector, this.eventVector);
        double angleDiff = getAngleDiff(instVector, this.eventVector);

        if(magDiff<MIN_MAG_DIFF && angleDiff<MIN_ANGLE_DIFF) {
            return 1;
        }

        return 0;

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


    private boolean isZeroMagnitude(RealVector vector) {
        double mag = vector.getNorm();
        if(mag==0) {
            return true;
        }
        return false;
    }

    @Override
    public Idea getInstance(List<Idea> constraints) {
        return null;
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

    private RealVector extractEventVector(Idea timeSteps) {
        int nSteps = timeSteps.getL().size();

        Idea firstStep = timeSteps.getL().get(0);
        Idea lastStep = timeSteps.getL().get(nSteps-1);

        Idea sPropertyState = firstStep.get(this.propertyName);
        Idea ePropertyState = lastStep.get(this.propertyName);

        double[] rawVector = new double[sPropertyState.getL().size()];

        if(sPropertyState.getL().size()==ePropertyState.getL().size()) {
            for(int i=0; i<sPropertyState.getL().size(); i++) {
                double startValue = (double) sPropertyState.getL().get(i).getValue();
                double endValue = (double) ePropertyState.getL().get(i).getValue();
                rawVector[i] = endValue-startValue;
            }
        } else {
            System.out.println("Property with inconsistent number of quality dimensions");
        }
        RealVector eventVector = new ArrayRealVector(rawVector);

        return eventVector;
    }

    boolean equals(EventCategory compCat) {
        if(this.propertyName != compCat.propertyName) {
            return false;
        }

        RealVector compVector = compCat.eventVector;


        if(isZeroMagnitude(compVector) && isZeroMagnitude(this.eventVector)) {
            return true;
        }

        double magDiff = getMagnitudeDiff(compVector, this.eventVector);
        double angleDiff = getAngleDiff(compVector, this.eventVector);

        if(magDiff<MIN_MAG_DIFF && angleDiff<MIN_ANGLE_DIFF) {
            return true;
        }

        return false;
    }

}
