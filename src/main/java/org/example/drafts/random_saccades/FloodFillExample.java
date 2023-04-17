package org.example.drafts.random_saccades;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.example.util.MatBufferedImageConverter.BufferedImage2Mat;

public class FloodFillExample {
    public static void main(String[] args) throws IOException {
        loadOpenCVLibraryFromCurrentPath();

        // Read the input image
//        Mat image = Imgcodecs.imread("river_raid_frame.png");
        BufferedImage imgSrc= ImageIO.read(FloodFillExample.class.getClassLoader().getResource("template_img_river.tiff"));
        Mat image = BufferedImage2Mat(imgSrc);

        // Convert to grayscale
//        Mat gray = new Mat();
////        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Perform floodfill
//        Mat mask = new Mat(image.rows() + 2, image.cols() + 2, CvType.CV_8UC1, new Scalar(0));
        Mat mask = Mat.zeros(image.rows() + 2, image.cols() + 2, 0);
//        mask.create(gray.rows() + 2, gray.cols() + 2, CvType.CV_8UC1);
        Scalar fillColor = new Scalar(0, 102, 255);

        // Define floodfill parameters
        int loDiff = 0;
        int upDiff = 0;
        int flags = 4 + (255 << 8);

        Rect rect = new Rect();

        // Perform floodfill
        Imgproc.floodFill(image, mask, new Point(150, 255), fillColor, rect, new Scalar(loDiff, loDiff, loDiff), new Scalar(upDiff, upDiff, upDiff), flags);
        System.out.println(rect.size());

        Rect roi = new Rect(1, 1, mask.cols() - 2, mask.rows() - 2);
        mask = mask.submat(roi);
        HighGui.imshow("Contour Mask", mask);

        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);


        // Draw contours on the image
        Mat result = image.clone();
        Imgproc.drawContours(image, contours, -1, new Scalar(0, 0, 255), 2);
//        Imgproc.rectangle(image, rect, new Scalar(0, 255, 0), 3);

        HighGui.imshow("Contour Detection", image);
        HighGui.waitKey();
        System.exit(0);
    }

    public static void loadOpenCVLibraryFromCurrentPath()   {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.load(s+"/libs/libopencv_java455.so");
    }
}



