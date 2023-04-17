package org.example.drafts.random_saccades;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;

import javax.imageio.ImageIO;
import java.awt.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.example.util.MatBufferedImageConverter.BufferedImage2Mat;

public class RandomSaccades {
    public static void main(String[] args) throws IOException, InterruptedException {
        loadOpenCVLibraryFromCurrentPath();

        HighGui.namedWindow("Random Saccades");
        // Read the input image
//        Mat image = Imgcodecs.imread("river_raid_frame.png");
        BufferedImage imgSrc= ImageIO.read(RandomSaccades.class.getClassLoader().getResource("rivererror.tiff"));
        Mat image = BufferedImage2Mat(imgSrc);


        while(true) {

//            Display the result
            HighGui.imshow("Random Saccades", updateRandomFloodFill(image));
            HighGui.waitKey(1);
            Thread.sleep(1);
        }
    }

    private static Mat updateRandomFloodFill(Mat image) {
        Mat floodFiledImage = image.clone();

        Mat mask = Mat.zeros(floodFiledImage.rows() + 2, floodFiledImage.cols() + 2, 0);

        Point seedPoint = new RandomForegroundPosition().getRandomForegroundPosition(image);
        Scalar fillColor = new Scalar(0, 102, 255);

        // Define floodfill parameters
        int loDiff = 0;
        int upDiff = 0;
        int flags = 4 | (255 << 8) + Imgproc.FLOODFILL_MASK_ONLY;

        // Perform floodfill
        Imgproc.floodFill(floodFiledImage, mask, seedPoint, fillColor, new Rect(), new Scalar(loDiff, loDiff, loDiff), new Scalar(upDiff, upDiff, upDiff), flags);
        Imgproc.circle(floodFiledImage, seedPoint, 3, new Scalar(100,0,100), -1);

        Rect roi = new Rect(1, 1, mask.cols() - 2, mask.rows() - 2);
        mask = mask.submat(roi);

        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        Scalar contourColor = new Scalar(100,0,100);
        Imgproc.drawContours(floodFiledImage, contours, -1, contourColor, 2);


        return floodFiledImage;
    }

    public static void loadOpenCVLibraryFromCurrentPath()   {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.load(s+"/libs/libopencv_java455.so");
    }
}



