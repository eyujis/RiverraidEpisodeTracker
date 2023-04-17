package org.example.drafts.random_saccades;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import java.util.Random;


import java.util.ArrayList;
import java.util.List;

public class RandomForegroundPosition {
    private Scalar lowerBoundBlue = new Scalar(185-5,47-5,40-5);
    private Scalar upperBoundBlue = new Scalar(185+5,47+5,40+5);

    private Scalar lowerBoundGreen= new Scalar(24-5,95-5,53-5);
    private Scalar upperBoundGreen = new Scalar(64+5,156+5,111+5);

    public Point getRandomForegroundPosition(Mat image) {

        Mat maskForeground = new Mat();
        Mat maskBackgroundBlue = new Mat();
        Mat maskBackgroundGreen = new Mat();

        Core.inRange(image, lowerBoundBlue, upperBoundBlue, maskBackgroundBlue);
        Core.inRange(image, lowerBoundGreen, upperBoundGreen, maskBackgroundGreen);
        Core.add(maskBackgroundGreen, maskBackgroundBlue, maskForeground);
        // Mask inversion
        Core.bitwise_not(maskForeground, maskForeground);

        int height = maskForeground.height();
        int width = maskForeground.width();

        // Create a list of white pixel positions
        List<Point> whitePixelPositions = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double[] pixel = maskForeground.get(y, x);
                if (pixel[0] == 255.0) {
                    // If the pixel intensity is 255, add its position to the list
                    whitePixelPositions.add(new Point(x, y));
                }
            }
        }

        // Pick a random white pixel position
        Random random = new Random();
        Point randomWhitePixelPosition = whitePixelPositions.get(random.nextInt(whitePixelPositions.size()));
        return randomWhitePixelPosition;

    }

}
