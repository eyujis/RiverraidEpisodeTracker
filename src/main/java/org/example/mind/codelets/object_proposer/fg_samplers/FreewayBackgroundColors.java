package org.example.mind.codelets.object_proposer.fg_samplers;

import org.opencv.core.Scalar;

import java.util.ArrayList;

public class FreewayBackgroundColors {

    private Scalar lowerBoundBlack = new Scalar(0,0,0);
    private Scalar upperBoundBlack = new Scalar(0,0,0);

    private Scalar lowerBoundGray= new Scalar(215-80,215-80,215-80);
    private Scalar upperBoundGray = new Scalar(215+10,215+10,215+10);

    ArrayList<BackgroundColor> backgroundColors;

    public FreewayBackgroundColors() {
        backgroundColors.add(new BackgroundColor(lowerBoundBlack, upperBoundBlack));
        backgroundColors.add(new BackgroundColor(lowerBoundGray, upperBoundGray));
    }

    public ArrayList<BackgroundColor> getBackgroundColors() {
        return backgroundColors;
    }
}
