package org.example.mind.codelets.object_proposer.fg_samplers;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import java.util.List;

public class FGPositionSequentialSampler {
//    private Scalar lowerBoundBlue = new Scalar(185-5,47-5,40-5);
//    private Scalar upperBoundBlue = new Scalar(185+5,47+5,40+5);

    private Scalar lowerBoundBlue = new Scalar(0,0,0);
    private Scalar upperBoundBlue = new Scalar(1,1,1);

//    private Scalar lowerBoundGreen= new Scalar(24-5,95-5,53-5);
//    private Scalar upperBoundGreen = new Scalar(64+5,156+5,111+5);

    private Scalar lowerBoundGreen= new Scalar(215-80,215-80,215-80);
    private Scalar upperBoundGreen = new Scalar(215+10,215+10,215+10);

    Mat maskForeground = new Mat();
    Mat maskBackgroundBlue = new Mat();
    Mat maskBackgroundGreen = new Mat();
    List<Point> samplePositions;

    public FGPositionSequentialSampler(Mat image) {
        Core.inRange(image, lowerBoundBlue, upperBoundBlue, maskBackgroundBlue);
        Core.inRange(image, lowerBoundGreen, upperBoundGreen, maskBackgroundGreen);
        Core.add(maskBackgroundGreen, maskBackgroundBlue, maskForeground);
        // Mask inversion
        Core.bitwise_not(maskForeground, maskForeground);

    }

    public Point getFGPosition() {
        int height = maskForeground.height();
        int width = maskForeground.width();

        // Create a list of white pixel positions
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double[] pixel = maskForeground.get(y, x);
                if (pixel[0] == 255.0) {
                    // If the pixel intensity is 255, add its position to the list
                    return new Point(x, y);
                }
            }
        }
        return null;
    }

    public void removeMaskFromSample(Mat mask) {
        Core.subtract(maskForeground, mask, maskForeground);
    }

    public boolean isEmpty() {
        if(Core.countNonZero(maskForeground)==0) {return true;} else {return false;}
    }

}
