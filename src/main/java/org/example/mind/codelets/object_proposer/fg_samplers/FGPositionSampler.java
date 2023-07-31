package org.example.mind.codelets.object_proposer.fg_samplers;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class FGPositionSampler {
    private Scalar lowerBoundBlue = new Scalar(185-5,47-5,40-5);
    private Scalar upperBoundBlue = new Scalar(185+5,47+5,40+5);

    private Scalar lowerBoundGreen= new Scalar(24-5,95-5,53-5);
    private Scalar upperBoundGreen = new Scalar(64+5,156+5,111+5);

    Mat maskForeground = new Mat();
    Mat maskBackgroundBlue = new Mat();
    Mat maskBackgroundGreen = new Mat();
    List<Point> samplePositions;

    public FGPositionSampler(Mat image) {
        Core.inRange(image, lowerBoundBlue, upperBoundBlue, maskBackgroundBlue);
        Core.inRange(image, lowerBoundGreen, upperBoundGreen, maskBackgroundGreen);
        Core.add(maskBackgroundGreen, maskBackgroundBlue, maskForeground);
        // Mask inversion
        Core.bitwise_not(maskForeground, maskForeground);

        int height = maskForeground.height();
        int width = maskForeground.width();

        // Create a list of white pixel positions
        samplePositions = new ArrayList<>();
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                double[] pixel = maskForeground.get(y, x);
                if (pixel[0] == 255.0) {
                    // If the pixel intensity is 255, add its position to the list
                    samplePositions.add(new Point(x, y));
                }
            }
        }
    }

    public Point getRandomFGPosition() {
        // Pick a random white pixel position
        Random random = new Random();
        Point randomWhitePixelPosition = samplePositions.get(random.nextInt(samplePositions.size()));
        return randomWhitePixelPosition;

    }

    public void removeMaskFromSample(Mat mask) {

        int height = mask.height();
        int width = mask.width();

        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                double[] pixel = mask.get(y, x);
                if (pixel[0] == 255.0) {
                    for (Iterator<Point> iter = samplePositions.iterator(); iter.hasNext(); ) {
                        Point samplePosition = iter.next();
                        if (samplePosition.x == x && samplePosition.y == y) {
                            iter.remove();
                        }
                    }
                }
            }
        }
    }

    public boolean isEmpty() {
        if(samplePositions.isEmpty()) {return true;} else {return false;}
    }

}
