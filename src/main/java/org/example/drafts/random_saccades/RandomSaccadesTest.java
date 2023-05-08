package org.example.drafts.random_saccades;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.example.util.MatBufferedImageConverter.BufferedImage2Mat;

public class RandomSaccadesTest {
    public static Mat mask1;
    public static Mat mask2;
    public static void main(String[] args) throws IOException, InterruptedException {
        loadOpenCVLibraryFromCurrentPath();

        HighGui.namedWindow("Random Saccades");
        HighGui.namedWindow("Before");
        HighGui.namedWindow("After");
        // Read the input image
//        Mat image = Imgcodecs.imread("river_raid_frame.png");
        BufferedImage imgSrc= ImageIO.read(RandomSaccadesTest.class.getClassLoader().getResource("rivererror.tiff"));
        Mat image = BufferedImage2Mat(imgSrc);

        RandomForegroundPosition randomForegroundPosition = new RandomForegroundPosition(image);

        while(!randomForegroundPosition.isEmpty()) {
//            Display the result
            HighGui.imshow("Random Saccades", updateRandomFloodFill(image, randomForegroundPosition));
            HighGui.imshow("Before", mask1);
            HighGui.imshow("After", mask2);
            HighGui.waitKey(1);
            Thread.sleep(500);
        }
        HighGui.destroyAllWindows();
        System.out.println("Finished");
    }

    private static Mat updateRandomFloodFill(Mat image, RandomForegroundPosition randomForegroundPosition) {
        Mat floodFiledImage = image.clone();
        Mat mask = Mat.zeros(floodFiledImage.rows() + 2, floodFiledImage.cols() + 2, 0);

        Point seedPoint = randomForegroundPosition.getRandomForegroundPosition();

        Scalar fillColor = new Scalar(127, 0, 255);

        // Define floodfill parameters
        int loDiff = 0;
        int upDiff = 0;
        int flags = 4 | (255 << 8) + Imgproc.FLOODFILL_MASK_ONLY;
//        int flags = 4 | (255 << 8);


        // Perform floodfill
        Imgproc.floodFill(floodFiledImage, mask, seedPoint, fillColor, new Rect(), new Scalar(loDiff, loDiff, loDiff), new Scalar(upDiff, upDiff, upDiff), flags);
        Imgproc.circle(floodFiledImage, seedPoint, 3, new Scalar(100,0,100), -1);
        Rect roi = new Rect(1, 1, mask.cols() - 2, mask.rows() - 2);
        mask = mask.submat(roi);

        mask1 = randomForegroundPosition.getSampleAsMat(mask);
        randomForegroundPosition.removeMaskFromSample(mask);
        mask2 = randomForegroundPosition.getSampleAsMat(mask);

        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        Scalar contourColor = new Scalar(100,0,100);
        Imgproc.drawContours(floodFiledImage, contours, -1, contourColor, 1);

        return floodFiledImage;
    }

    public static void loadOpenCVLibraryFromCurrentPath()   {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.load(s+"/libs/libopencv_java455.so");
    }
}



