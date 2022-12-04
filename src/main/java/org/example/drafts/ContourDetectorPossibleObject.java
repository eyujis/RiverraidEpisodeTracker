package org.example.drafts;

import org.example.objectDetection.PossibleObject;
import org.example.util.MatBufferedImageConverter;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ContourDetectorPossibleObject {
    private int threshold = 40;
    private Random rng = new Random(12345);

    public ContourDetectorPossibleObject() {

    }

    public BufferedImage addContours(BufferedImage img) throws IOException {
        Mat matImage = MatBufferedImageConverter.BufferedImage2Mat(img);
        Mat matImageGray = new Mat();
        Imgproc.cvtColor(matImage, matImageGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(matImageGray, matImageGray, new Size(2, 2));

        Mat cannyOutput = new Mat();
        Imgproc.Canny(matImageGray, cannyOutput, threshold, threshold * 2);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        List<PossibleObject> possibleObjectArrayList = new ArrayList<>();

        for(int i = 0; i<contours.size(); i++) {
            PossibleObject possibleObject = new PossibleObject(contours.get(i));
            possibleObjectArrayList.add(possibleObject);
        }

        Mat drawing = Mat.zeros(matImage.size(), CvType.CV_8UC3);

        for (int i = 0; i < contours.size(); i++) {
            Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
            Imgproc.rectangle(drawing, possibleObjectArrayList.get(i).getBoundRect().tl(), possibleObjectArrayList.get(i).getBoundRect().br(), color, 1);
        }

        BufferedImage bufferedImage = MatBufferedImageConverter.Mat2BufferedImage(drawing);
        return bufferedImage;
    }
}
