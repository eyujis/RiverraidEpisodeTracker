package org.example.mind.codelets.object_cat_learner.entities;

import org.opencv.core.Scalar;

import java.util.Random;

public abstract class ObjectCategory {
    static int id = 0;
    int categoryId;
    Scalar colorIdScalar;
    double relevance;

    void initializeColorId() {
        Random rng = new Random();
        // Random colors closer to white for avoiding dark contours with black background.
        this.colorIdScalar = new Scalar(rng.nextInt(231) + 25,
                rng.nextInt(231) + 25,
                rng.nextInt(231) + 25);
    }

    void initializeCategoryId() {
        this.categoryId = id;
        id++;
    }

    public int getCategoryId() {
        return this.categoryId;
    }

    public Scalar getColorIdScalar() {
        return this.colorIdScalar;
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

    public void setColorIdScalar(Scalar newIdColor) {
        this.colorIdScalar = newIdColor;
    }

}
