//package org.example.drafts;
//
//import org.opencv.core.*;
//import org.opencv.core.Point;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//
//public class ContourDetectorForTest {
//    private int threshold = 40;
//    private Random rng = new Random(12345);
//
//    public ContourDetectorForTest() {
//
//    }
//
//    public BufferedImage addContours(BufferedImage img) throws IOException {
//        Mat matImage = BufferedImage2Mat(img);
//        Mat matImageGray = new Mat();
//
//        Imgproc.cvtColor(matImage, matImageGray, Imgproc.COLOR_BGR2GRAY);
////        Imgproc.blur(matImageGray, matImageGray, new Size(3, 3));
//        Imgproc.blur(matImageGray, matImageGray, new Size(2, 2));
//
//        Mat cannyOutput = new Mat();
//        Imgproc.Canny(matImageGray, cannyOutput, threshold, threshold * 2);
//
//        List<MatOfPoint> contours = new ArrayList<>();
//        Mat hierarchy = new Mat();
//        Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//        MatOfPoint2f[] contoursPoly  = new MatOfPoint2f[contours.size()];
//        Rect[] boundRect = new Rect[contours.size()];
//        org.opencv.core.Point[] centers = new org.opencv.core.Point[contours.size()];
//        float[][] radius = new float[contours.size()][1];
//        for (int i = 0; i < contours.size(); i++) {
//            contoursPoly[i] = new MatOfPoint2f();
//            Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), contoursPoly[i], 3, true);
//            boundRect[i] = Imgproc.boundingRect(new MatOfPoint(contoursPoly[i].toArray()));
//            centers[i] = new Point();
//            Imgproc.minEnclosingCircle(contoursPoly[i], centers[i], radius[i]);
//        }
//        Mat drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3);
//
////        List<MatOfPoint> contoursPolyList = new ArrayList<>(contoursPoly.length);
////        for (MatOfPoint2f poly : contoursPoly) {
////            contoursPolyList.add(new MatOfPoint(poly.toArray()));
////        }
//        for (int i = 0; i < contours.size(); i++) {
//            Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
////            Imgproc.drawContours(drawing, contoursPolyList, i, color);
//            Imgproc.rectangle(drawing, boundRect[i].tl(), boundRect[i].br(), color, 1);
//            System.out.println(boundRect[i].br());
////            Imgproc.circle(drawing, centers[i], (int) radius[i][0], color, 1);
//        }
//
//
//        BufferedImage bufferedImage = Mat2BufferedImage(drawing);
//        return bufferedImage;
//    }
//
//    public static Mat BufferedImage2Mat(BufferedImage image) throws IOException {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        ImageIO.write(image, "jpg", byteArrayOutputStream);
//        byteArrayOutputStream.flush();
//        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
//    }
//    //testar
//    public static BufferedImage Mat2BufferedImage(Mat matrix)throws IOException {
//        MatOfByte mob=new MatOfByte();
//        Imgcodecs.imencode(".tiff", matrix, mob);
//        return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
//    }
//}
