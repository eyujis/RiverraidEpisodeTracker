package org.example.drafts.masking_contour;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.example.util.MatBufferedImageConverter.BufferedImage2Mat;

public class ContourDetection_v2 {
    public static void main(String[] args) throws IOException {
        loadOpenCVLibraryFromCurrentPath();

        // Read the input image
//        Mat image = Imgcodecs.imread("river_raid_frame.png");
        BufferedImage imgSrc= ImageIO.read(ContourDetection_v2.class.getClassLoader().getResource("template_img_river.tiff"));
        Mat image = BufferedImage2Mat(imgSrc);

        // Define the color range for the objects of interest
        // Inverted RGB -> BGR
        Scalar lowerBoundBlue = new Scalar(185-5,47-5,40-5);
        Scalar upperBoundBlue = new Scalar(185+5,47+5,40+5);

        Scalar lowerBoundGreen= new Scalar(24-5,95-5,53-5);
        Scalar upperBoundGreen = new Scalar(64+5,156+5,111+5);

        // Threshold the image to extract the regions of interest
        Mat maskObjects = new Mat();
        Mat maskBackgroundBlue = new Mat();
        Mat maskBackgroundGreen = new Mat();

        Core.inRange(image, lowerBoundBlue, upperBoundBlue, maskBackgroundBlue);
        HighGui.imshow("Mask Blue", maskBackgroundBlue);
        Core.inRange(image, lowerBoundGreen, upperBoundGreen, maskBackgroundGreen);
        HighGui.imshow("Mask Green", maskBackgroundGreen);
        Core.add(maskBackgroundGreen, maskBackgroundBlue, maskObjects);
        HighGui.imshow("Mask Result", maskObjects);
        // Mask inversion
        Core.bitwise_not(maskObjects, maskObjects);


//         Find object contours in the mask
        Mat objectsHierarchy = new Mat();
        List<MatOfPoint> objectContours = new ArrayList<>();
        Imgproc.findContours(maskObjects, objectContours, objectsHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);


        // Draw the contours on the original image
        Mat contourImage = image.clone();
        for (int i = 0; i < objectContours.size(); i++) {
            Imgproc.drawContours(contourImage, objectContours, i, new Scalar(0, 0, 255), 2);
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



