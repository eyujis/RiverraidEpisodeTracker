package org.example.mind.codelets.object_detection;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.List;

public class MaskedContourDectector {

    public List<MatOfPoint> getContoursFromFrame(Mat frameImage) {

        // Inverted RGB -> BGR
        Scalar lowerBoundBlue = new Scalar(185-5,47-5,40-5);
        Scalar upperBoundBlue = new Scalar(185+5,47+5,40+5);

        // Dark green
        Scalar lowerBoundGreen= new Scalar(24-5,95-5,53-5);
        // Green
        Scalar upperBoundGreen = new Scalar(64+5,156+5,111+5);

        Mat maskBackgroundBlue = new Mat();
        Mat maskBackgroundGreen = new Mat();

        Core.inRange(frameImage, lowerBoundBlue, upperBoundBlue, maskBackgroundBlue);
        Core.inRange(frameImage, lowerBoundGreen, upperBoundGreen, maskBackgroundGreen);

        // Find contours in the mask
        Mat hierarchyBlueBackground = new Mat();
        List<MatOfPoint> blueBackgroundContours = new ArrayList<>();
        Imgproc.findContours(maskBackgroundBlue, blueBackgroundContours, hierarchyBlueBackground, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find contours in the mask
        Mat hierarchyGreenBackground = new Mat();
        List<MatOfPoint> greenBackgroundContours = new ArrayList<>();
        Imgproc.findContours(maskBackgroundGreen, greenBackgroundContours, hierarchyGreenBackground, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        List<MatOfPoint> resultContours = new ArrayList<>();
        resultContours.addAll(blueBackgroundContours);
        resultContours.addAll(greenBackgroundContours);

        return resultContours;
    }



}
