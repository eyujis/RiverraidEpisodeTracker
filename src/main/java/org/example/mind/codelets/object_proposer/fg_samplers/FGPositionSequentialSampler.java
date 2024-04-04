package org.example.mind.codelets.object_proposer.fg_samplers;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class FGPositionSequentialSampler {
    private Scalar lowerBoundBlue = new Scalar(185-5,47-5,40-5);
    private Scalar upperBoundBlue = new Scalar(185+5,47+5,40+5);

    private Scalar lowerBoundGreen= new Scalar(24-5,95-5,53-5);
    private Scalar upperBoundGreen = new Scalar(64+5,156+5,111+5);

    private Scalar lowerBoundYellow= new Scalar(74,232,232);
    private Scalar upperBoundYellow = new Scalar(74,232,232);

    private Scalar lowerBoundLightGrey = new Scalar(170,170,170);
    private Scalar upperBoundLightGrey = new Scalar(170,170,170);

    private Scalar lowerBoundDarkGrey = new Scalar(111,111,111);
    private Scalar upperBoundDarkGrey = new Scalar(111,111,111);

    Mat maskForeground = new Mat();
    Mat maskBackgroundBlue = new Mat();
    Mat maskBackgroundGreen = new Mat();
    Mat maskBackgroundYellow = new Mat();
    Mat maskBackgroundLightGrey = new Mat();
    Mat maskBackgroundDarkGrey = new Mat();

    public FGPositionSequentialSampler(Mat image) {
        Core.inRange(image, lowerBoundBlue, upperBoundBlue, maskBackgroundBlue);
        Core.inRange(image, lowerBoundGreen, upperBoundGreen, maskBackgroundGreen);
        Core.inRange(image, lowerBoundLightGrey, upperBoundLightGrey, maskBackgroundLightGrey);
        Core.inRange(image, lowerBoundDarkGrey, upperBoundDarkGrey, maskBackgroundDarkGrey);

        Core.add(maskBackgroundGreen, maskBackgroundBlue, maskForeground);
        Core.add(maskBackgroundLightGrey, maskForeground, maskForeground);
        Core.add(maskBackgroundDarkGrey, maskForeground, maskForeground);

        Core.bitwise_not(maskForeground, maskForeground);
        removeYellowStripeFromStreet(image);
    }

    public void removeYellowStripeFromStreet(Mat image) {
        // Create mask for yellow color
        Mat maskYellow = new Mat();
        Core.inRange(image, lowerBoundYellow, upperBoundYellow, maskYellow);

        // Create mask for grey color
        Mat maskGrey = new Mat();
        Core.inRange(image, lowerBoundLightGrey, upperBoundLightGrey, maskGrey);
        Mat hierarchy = new Mat();

        // Find contours of yellow and grey objects
        List<MatOfPoint> contoursYellow = new ArrayList<>();
        List<MatOfPoint> contoursGrey = new ArrayList<>();
        Imgproc.findContours(maskYellow, contoursYellow, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.findContours(maskGrey, contoursGrey, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Create a mask to cover yellow area if touching
        Mat maskRemoveYellow = new Mat(image.size(), CvType.CV_8UC1, Scalar.all(0));

        for(MatOfPoint contourYellow : contoursYellow) {
            for(MatOfPoint contourGrey : contoursGrey) {
                Rect boundingRect = Imgproc.boundingRect(contourGrey);

                // in the case the bounding rect is small (e.g. grey particle in an explosion)
                if(boundingRect.width < 5 && boundingRect.height < 5) {
                    break;
                }

                boolean touching = false;

                for(Point yellowPoint : contourYellow.toList()) {
                    for(Point greyPoint : contourGrey.toList()) {
                        double distance = Math.sqrt(Math.pow(yellowPoint.x - greyPoint.x, 2) + Math.pow(yellowPoint.y - greyPoint.y, 2));

                        if(distance == 1) {
                            Imgproc.drawContours(maskRemoveYellow, contoursYellow, contoursYellow.indexOf(contourYellow), Scalar.all(255), -1);
                            touching = true;
                            break;
                        }
                    }
                }
                if(touching) {
                    break;
                }
            }
        }

        Core.bitwise_not(maskRemoveYellow, maskRemoveYellow);
        Core.bitwise_and(maskRemoveYellow, maskForeground, maskForeground);
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
