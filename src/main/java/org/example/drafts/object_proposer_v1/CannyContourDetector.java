package org.example.drafts.object_proposer_v1;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class CannyContourDetector {
    private int threshold = 40;
    public List<MatOfPoint> getContoursFromFrame(Mat frameImage) {
        Mat frameImageGray = new Mat();
        Imgproc.cvtColor(frameImage, frameImageGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(frameImageGray, frameImageGray, new Size(2, 2));

        Mat cannyOutput = new Mat();
        Imgproc.Canny(frameImageGray, cannyOutput, threshold, threshold * 2);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        return contours;
    }
}
