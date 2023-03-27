package org.example.drafts.general_contours;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import static org.example.util.MatBufferedImageConverter.BufferedImage2Mat;

class GeneralContours1 {
    private Mat src = new Mat();
    private Mat srcGray = new Mat();
    private JFrame frame;
    private JLabel imgSrcLabel;
    private JLabel imgContoursLabel;
    private static final int MAX_THRESHOLD = 255;
    private int threshold = 100;
    private Random rng = new Random(12345);
    public GeneralContours1(String[] args) throws IOException {
//        String filename = args.length > 0 ? args[0] : "../data/stuff.jpg";
//        Mat src = Imgcodecs.imread(filename);
        BufferedImage imgSrc= ImageIO.read(getClass().getClassLoader().getResource("template_img_river.tiff"));
        src = BufferedImage2Mat(imgSrc);

//        if (src.empty()) {
//            System.err.println("Cannot read image: " + filename);
//            System.exit(0);
//        }

//        Mat kernel = new Mat(3, 3, CvType.CV_32F);
//        // an approximation of second derivative, a quite strong kernel
//        float[] kernelData = new float[(int) (kernel.total() * kernel.channels())];
//        kernelData[0] = 1; kernelData[1] = 1; kernelData[2] = 1;
//        kernelData[3] = 1; kernelData[4] = -8; kernelData[5] = 1;
//        kernelData[6] = 1; kernelData[7] = 1; kernelData[8] = 1;
//        kernel.put(0, 0, kernelData);
//        Mat imgLaplacian = new Mat();
//        Imgproc.filter2D(src, imgLaplacian, CvType.CV_32F, kernel);
//        Mat sharp = new Mat();
//        src.convertTo(sharp, CvType.CV_32F);
//        Mat imgResult = new Mat();
//        Core.subtract(sharp, imgLaplacian, imgResult);
//        // convert back to 8bits gray scale
//        imgResult.convertTo(imgResult, CvType.CV_8UC3);
//        imgLaplacian.convertTo(imgLaplacian, CvType.CV_8UC3);
//        // imshow( "Laplace Filtered Image", imgLaplacian );

        Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.blur(srcGray, srcGray, new Size(3, 3));
        // Create and set up the window.
        frame = new JFrame("Creating Bounding boxes and circles for contours demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set up the content pane.
        Image img = HighGui.toBufferedImage(src);
        addComponentsToPane(frame.getContentPane(), img);
        // Use the content pane's default BorderLayout. No need for
        // setLayout(new BorderLayout());
        // Display the window.
        frame.pack();
        frame.setVisible(true);
        update();
    }
    private void addComponentsToPane(Container pane, Image img) {
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
        sliderPanel.add(new JLabel("Canny threshold: "));
        JSlider slider = new JSlider(0, MAX_THRESHOLD, threshold);
        slider.setMajorTickSpacing(20);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                threshold = source.getValue();
                update();
            }
        });
        sliderPanel.add(slider);
        pane.add(sliderPanel, BorderLayout.PAGE_START);
        JPanel imgPanel = new JPanel();
        imgSrcLabel = new JLabel(new ImageIcon(img));
        imgPanel.add(imgSrcLabel);
        Mat blackImg = Mat.zeros(srcGray.size(), CvType.CV_8U);
        imgContoursLabel = new JLabel(new ImageIcon(HighGui.toBufferedImage(blackImg)));
        imgPanel.add(imgContoursLabel);
        pane.add(imgPanel, BorderLayout.CENTER);
    }
    private void update() {
        Mat cannyOutput = new Mat();
//        Imgproc.Canny(srcGray, cannyOutput, threshold, threshold * 2);
        double threshold1 = Imgproc.threshold(srcGray, cannyOutput, (double) threshold, (double) threshold * 2, 0);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        MatOfPoint2f[] contoursPoly  = new MatOfPoint2f[contours.size()];
        Rect[] boundRect = new Rect[contours.size()];
        Point[] centers = new Point[contours.size()];
        float[][] radius = new float[contours.size()][1];
        for (int i = 0; i < contours.size(); i++) {
            contoursPoly[i] = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), contoursPoly[i], 3, true);
            boundRect[i] = Imgproc.boundingRect(new MatOfPoint(contoursPoly[i].toArray()));
            centers[i] = new Point();
            Imgproc.minEnclosingCircle(contoursPoly[i], centers[i], radius[i]);
        }
        Mat drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3);
        List<MatOfPoint> contoursPolyList = new ArrayList<>(contoursPoly.length);
        for (MatOfPoint2f poly : contoursPoly) {
            contoursPolyList.add(new MatOfPoint(poly.toArray()));
        }
        for (int i = 0; i < contours.size(); i++) {
            Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
            Imgproc.drawContours(drawing, contoursPolyList, i, color);
            Imgproc.rectangle(drawing, boundRect[i].tl(), boundRect[i].br(), color, 2);
            Imgproc.circle(drawing, centers[i], (int) radius[i][0], color, 2);
        }
        imgContoursLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(drawing)));
        frame.repaint();
    }
}
public class GeneralContoursDemo1 {
    public static void main(String[] args) {
        // Load the native OpenCV library
        loadOpenCVLibraryFromCurrentPath();
        // Schedule a job for the event dispatch thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new GeneralContours1(args);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static void loadOpenCVLibraryFromCurrentPath()   {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.load(s+"/libs/libopencv_java455.so");
    }
}