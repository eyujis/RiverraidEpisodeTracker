package org.example.mind.codelets.object_proposer_utils;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class MaskedContourDectector_v2 {

    public List<MatOfPoint> getContoursFromFrame(Mat frameImage) {

        // Inverted RGB -> BGR
        Scalar lowerBoundBlue = new Scalar(185-5,47-5,40-5);
        Scalar upperBoundBlue = new Scalar(185+5,47+5,40+5);

        Scalar lowerBoundGreen= new Scalar(24-5,95-5,53-5);
        Scalar upperBoundGreen = new Scalar(64+5,156+5,111+5);

        // Threshold the image to extract the regions of interest
        Mat maskObjects = new Mat();
        Mat maskBackgroundBlue = new Mat();
        Mat maskBackgroundGreen = new Mat();

        Core.inRange(frameImage, lowerBoundBlue, upperBoundBlue, maskBackgroundBlue);
        HighGui.imshow("Mask Blue", maskBackgroundBlue);
        Core.inRange(frameImage, lowerBoundGreen, upperBoundGreen, maskBackgroundGreen);
        HighGui.imshow("Mask Green", maskBackgroundGreen);
        Core.add(maskBackgroundGreen, maskBackgroundBlue, maskObjects);
        HighGui.imshow("Mask Result", maskObjects);
        // Mask inversion
        Core.bitwise_not(maskObjects, maskObjects);


//         Find object contours in the mask
        Mat objectsHierarchy = new Mat();
        List<MatOfPoint> objectContours = new ArrayList<>();
        Imgproc.findContours(maskObjects, objectContours, objectsHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        return objectContours;
    }



}
