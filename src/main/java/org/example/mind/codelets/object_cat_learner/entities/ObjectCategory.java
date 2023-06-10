package org.example.mind.codelets.object_cat_learner.entities;

import br.unicamp.cst.representation.idea.Category;
import org.opencv.core.Scalar;

import java.util.Random;

public abstract class ObjectCategory implements Category {
    double relevance;

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
