package org.example.view;

import org.example.drafts.ContourDetectorPossibleObject;
import org.example.objectDetection.ExistentObject;
import org.example.objectDetection.ObjectListProposer;
import org.example.objectDetection.PossibleObject;
import org.example.socket.SocketImgRcvr;
import org.example.util.MatBufferedImageConverter;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ButtonStartRcvImg implements ActionListener {

    private JLabel socketImgLbl;
    private JLabel contourImgLbl;
    private JLabel contourConcatImgLbl;
    private ScheduledExecutorService timer;

    private SocketImgRcvr socketImgRcvr;

    private ContourDetectorPossibleObject contourDetector;

    private ObjectListProposer objectListProposer;

    public ButtonStartRcvImg(JLabel socketImgLbl, JLabel contourImgLbl, JLabel contourConcatImgLbl) throws IOException {
        this.socketImgLbl = socketImgLbl;
        this.contourImgLbl = contourImgLbl;
        this.contourConcatImgLbl = contourConcatImgLbl;
        this.objectListProposer = new ObjectListProposer();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Runnable frameGrabber = new Runnable() {
            @Override public void run() {
                try {
                    BufferedImage imgFromSocket = new SocketImgRcvr().receiveImage();
                    setImgToSocketImgLbl(imgFromSocket);

                    objectListProposer.update(MatBufferedImageConverter.BufferedImage2Mat(imgFromSocket));
                    List<ExistentObject> existentObjectList = objectListProposer.getExistentObjectListFromCurrentFrame();
                    setImgToContourImgLbl(buffImageFromObjectList(existentObjectList, MatBufferedImageConverter.BufferedImage2Mat(imgFromSocket)));

//                    setImgToContourImgLbl(contourDetector.addContours(imgFromSocket));


                } catch (IOException ex) {
                    System.out.println(ex);
                    throw new RuntimeException(ex);
                }
            }
        };

        this.timer = Executors.newSingleThreadScheduledExecutor();
        this.timer.scheduleAtFixedRate(frameGrabber, 0, 10, TimeUnit.MILLISECONDS);
    }

    public void setImgToSocketImgLbl(BufferedImage imgToSet)  {
        socketImgLbl.setIcon(new ImageIcon(imgToSet));
//        imgFromSocket.revalidate();
//        imgFromSocket.repaint();
//        imgFromSocket.update(imgFromSocket.getGraphics());
    }

    public void setImgToContourImgLbl(BufferedImage imgToSet)  {
        contourImgLbl.setIcon(new ImageIcon(imgToSet));
//        imgFromSocket.revalidate();
//        imgFromSocket.repaint();
//        imgFromSocket.update(imgFromSocket.getGraphics());
    }

    public void setImgToContourConcatImgLbl(BufferedImage imgToSet)  {
        contourImgLbl.setIcon(new ImageIcon(imgToSet));
    }


    public BufferedImage buffImageFromObjectList(List<ExistentObject> existentObjectList, Mat matImage) throws IOException {
        Mat drawing = Mat.zeros(matImage.size(), CvType.CV_8UC3);

        for (int i = 0; i < existentObjectList.size(); i++) {
//            Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
            Scalar color = existentObjectList.get(i).getCurrentFramePossibleObject().getColor();
            Imgproc.rectangle(drawing,
                    existentObjectList.get(i).getCurrentFramePossibleObject().getBoundRect().tl(),
                    existentObjectList.get(i).getCurrentFramePossibleObject().getBoundRect().br(), color, 1);
        }

        BufferedImage bufferedImage = MatBufferedImageConverter.Mat2BufferedImage(drawing);

        return bufferedImage;
    }
}
