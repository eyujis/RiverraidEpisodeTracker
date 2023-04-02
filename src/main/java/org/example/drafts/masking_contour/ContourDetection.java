package org.example.drafts.masking_contour;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.example.util.MatBufferedImageConverter.BufferedImage2Mat;

public class ContourDetection {
    public static void main(String[] args) throws IOException {
        loadOpenCVLibraryFromCurrentPath();

        // Read the input image
//        Mat image = Imgcodecs.imread("river_raid_frame.png");
        BufferedImage imgSrc= ImageIO.read(ContourDetection.class.getClassLoader().getResource("template_img_river.tiff"));
        Mat image = BufferedImage2Mat(imgSrc);

        // Define the color range for the objects of interest
        // Inverted RGB -> BGR
        Scalar lowerBoundBlue = new Scalar(185-5,47-5,40-5);
        Scalar upperBoundBlue = new Scalar(185+5,47+5,40+5);

        Scalar lowerBoundGreen= new Scalar(24-5,95-5,53-5);
        Scalar upperBoundGreen = new Scalar(64+5,156+5,111+5);

        // Threshold the image to extract the regions of interest
//        Mat maskBlue = new Mat();
//        Mat maskGreen = new Mat();
//        Mat maskObjects = new Mat();
        Mat maskBackgroundBlue = new Mat();
        Mat maskBackgroundGreen = new Mat();

//        Core.inRange(image, lowerBoundBlue, upperBoundBlue, maskBlue);
//        HighGui.imshow("Mask Blue", maskBlue);
//        Core.inRange(image, lowerBoundGreen, upperBoundGreen, maskGreen);
//        HighGui.imshow("Mask Green", maskGreen);
//        Core.add(maskGreen, maskBlue, maskObjects);
//        HighGui.imshow("Mask Result", maskObjects);
//        // Mask inversion
//        Core.bitwise_not(maskObjects, maskObjects);

        Core.inRange(image, lowerBoundBlue, upperBoundBlue, maskBackgroundBlue);
        HighGui.imshow("Mask Blue Background", maskBackgroundBlue);
        Core.inRange(image, lowerBoundGreen, upperBoundGreen, maskBackgroundGreen);
        HighGui.imshow("Mask Green Background", maskBackgroundGreen);

        // Find object contours in the mask
//        Mat objectsHierarchy = new Mat();
//        List<MatOfPoint> objectContours = new ArrayList<>();
//        Imgproc.findContours(maskObjects, objectContours, objectsHierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);

        // Find contours in the mask
        Mat hierarchyBlueBackground = new Mat();
        List<MatOfPoint> blueBackgroundContours = new ArrayList<>();
        Imgproc.findContours(maskBackgroundBlue, blueBackgroundContours, hierarchyBlueBackground, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);

        // Find contours in the mask
        Mat hierarchyGreenBackground = new Mat();
        List<MatOfPoint> greenBackgroundContours = new ArrayList<>();
        Imgproc.findContours(maskBackgroundGreen, greenBackgroundContours, hierarchyGreenBackground, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);

        List<MatOfPoint> resultContours = new ArrayList<>();
//        resultContours.addAll(objectContours);
        resultContours.addAll(blueBackgroundContours);
        resultContours.addAll(greenBackgroundContours);

        // Draw the contours on the original image
        Mat contourImage = image.clone();
        for (int i = 0; i < resultContours.size(); i++) {
            Imgproc.drawContours(contourImage, resultContours, i, new Scalar(0, 0, 255), 2);
        }

//         Display the result
        HighGui.imshow("Contour Detection", contourImage);
        HighGui.waitKey();
        System.exit(0);
    }

    public static void loadOpenCVLibraryFromCurrentPath()   {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.load(s+"/libs/libopencv_java455.so");
    }
}



