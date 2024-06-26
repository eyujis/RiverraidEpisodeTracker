package org.example.drafts.random_saccades;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import java.util.Iterator;
import java.util.Random;


import java.util.ArrayList;
import java.util.List;

public class RandomForegroundPosition {
    private Scalar lowerBoundBlue = new Scalar(185-5,47-5,40-5);
    private Scalar upperBoundBlue = new Scalar(185+5,47+5,40+5);

    private Scalar lowerBoundGreen= new Scalar(24-5,95-5,53-5);
    private Scalar upperBoundGreen = new Scalar(64+5,156+5,111+5);

    Mat maskForeground = new Mat();
    Mat maskBackgroundBlue = new Mat();
    Mat maskBackgroundGreen = new Mat();
    List<Point> samplePositions;

    public RandomForegroundPosition(Mat image) {
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


    public Point getRandomForegroundPosition() {
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

    public Mat getSampleAsMat(Mat image) {
        Mat mask = Mat.zeros(image.rows(), image.cols(), 0);
        for(Point randomSamplePosition : samplePositions) {
            mask.put((int) randomSamplePosition.y, (int) randomSamplePosition.x, 255);
        }
        return mask;
    }

    public boolean isEmpty() {
        if(samplePositions.isEmpty()) {return true;} else {return false;}
    }

}
