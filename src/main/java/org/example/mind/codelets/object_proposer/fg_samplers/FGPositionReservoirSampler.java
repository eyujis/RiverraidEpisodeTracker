package org.example.mind.codelets.object_proposer.fg_samplers;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import java.util.Random;

public class FGPositionReservoirSampler {
    private Scalar lowerBoundBlue = new Scalar(185-5,47-5,40-5);
    private Scalar upperBoundBlue = new Scalar(185+5,47+5,40+5);

    private Scalar lowerBoundGreen= new Scalar(24-5,95-5,53-5);
    private Scalar upperBoundGreen = new Scalar(64+5,156+5,111+5);

    Mat maskForeground = new Mat();
    Mat maskBackgroundBlue = new Mat();
    Mat maskBackgroundGreen = new Mat();

    Random random = new Random();

    public FGPositionReservoirSampler(Mat image) {
        Core.inRange(image, lowerBoundBlue, upperBoundBlue, maskBackgroundBlue);
        Core.inRange(image, lowerBoundGreen, upperBoundGreen, maskBackgroundGreen);
        Core.add(maskBackgroundGreen, maskBackgroundBlue, maskForeground);
        // Mask inversion
        Core.bitwise_not(maskForeground, maskForeground);
    }

    public Point getRandomFGPosition() {
        int height = maskForeground.height();
        int width = maskForeground.width();
        Point randomPoint = new Point();
        int counter = 1;

        // Create a list of white pixel positions
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                double[] pixel = maskForeground.get(y, x);
                if (pixel[0] == 255.0) {
                    // Reservoir sample
                    if(random.nextInt(counter)<=counter) {
                        randomPoint = new Point(x, y);
                    }
                    counter++;
                }
            }
        }

        return randomPoint;

    }

    public void removeMaskFromSample(Mat mask) {
        Core.subtract(maskForeground, mask, maskForeground);
    }

    public boolean isEmpty() {
        int height = maskForeground.height();
        int width = maskForeground.width();

        // Create a list of white pixel positions
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                double[] pixel = maskForeground.get(y, x);
                if (pixel[0] == 255.0) {
                    return false;
                }
            }
        }
        return true;
    }

}
