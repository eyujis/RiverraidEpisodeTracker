package org.example.mind.codelets.object_proposer.fg_samplers;

import org.opencv.core.Scalar;


public class BackgroundColor {
    Scalar lowerBound;
    Scalar upperBound;

    public BackgroundColor(Scalar lowerBound, Scalar upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public Scalar getLowerBound() {
        return lowerBound;
    }

    public Scalar getUpperBound() {
        return upperBound;
    }
}
