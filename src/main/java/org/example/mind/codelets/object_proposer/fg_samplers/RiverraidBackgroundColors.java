package org.example.mind.codelets.object_proposer.fg_samplers;

import org.opencv.core.Scalar;

import java.util.ArrayList;

public class RiverraidBackgroundColors {

    private Scalar lowerBoundBlue = new Scalar(185-5,47-5,40-5);
    private Scalar upperBoundBlue = new Scalar(185+5,47+5,40+5);

    private Scalar lowerBoundGreen= new Scalar(24-5,95-5,53-5);
    private Scalar upperBoundGreen = new Scalar(64+5,156+5,111+5);

    ArrayList<BackgroundColor> backgroundColors;

    public RiverraidBackgroundColors() {
        backgroundColors.add(new BackgroundColor(lowerBoundBlue, upperBoundBlue));
        backgroundColors.add(new BackgroundColor(lowerBoundGreen, upperBoundGreen));
    }

    public ArrayList<BackgroundColor> getBackgroundColors() {
        return backgroundColors;
    }
}
