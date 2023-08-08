package org.example.mind.codelets.event_cat_learner;

import br.unicamp.cst.representation.idea.Category;
import br.unicamp.cst.representation.idea.Idea;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.List;

public class EventCategory implements Category {
    String propertyName;
    RealVector eventVector;
    double relevance;

    public EventCategory(String propertyName, Idea timeSteps, double relevance) {
        this.propertyName = propertyName;
        this.relevance = relevance;
        this.eventVector = extractEventVector(timeSteps);
        System.out.println(this.eventVector);
    }
    @Override
    public double membership(Idea timeSteps) { return 0;}

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

    public RealVector extractEventVector(Idea timeSteps) {
        int nSteps = timeSteps.getL().size();

        Idea firstStep = timeSteps.getL().get(0);
        Idea lastStep = timeSteps.getL().get(nSteps-1);

        Idea sPropertyState = firstStep.get(propertyName);
        Idea ePropertyState = lastStep.get(propertyName);

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
}
