package org.example.view;

import org.example.drafts.ContourDetectorPossibleObject;
import org.example.mind.codelets.object_detection.ComposedObject;
import org.example.mind.codelets.object_detection.ObjectListProposer;
import org.example.environment.socket.SocketFrameRcvr;
import org.example.util.MatBufferedImageConverter;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ButtonStartRcvImg implements ActionListener {

    private JLabel socketImgLbl;
    private JLabel contourImgLbl;
    private JLabel contourConcatImgLbl;
    private ScheduledExecutorService timer;

    private SocketFrameRcvr socketImgRcvr;

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
                    BufferedImage imgFromSocket = new SocketFrameRcvr().receiveImage();
                    setImgToSocketImgLbl(imgFromSocket);

                    objectListProposer.update(MatBufferedImageConverter.BufferedImage2Mat(imgFromSocket));
                    List<ComposedObject> composedObjectList = objectListProposer.getComposedObjectListFromCurrentFrame();
                    setImgToContourImgLbl(buffImageFromObjectList(composedObjectList, MatBufferedImageConverter.BufferedImage2Mat(imgFromSocket)));

                    List<ComposedObject> composedObjectConcatList = objectListProposer.getComposedObjectMergedListFromCurrentFrame();
                    setImgToContourConcatImgLbl(buffImageFromObjectList(composedObjectConcatList, MatBufferedImageConverter.BufferedImage2Mat(imgFromSocket)));

//                    setImgToContourImgLbl(contourDetector.addContours(imgFromSocket));


                } catch (IOException ex) {
                    System.out.println(ex);
                    throw new RuntimeException(ex);
                }
            }
        };

        this.timer = Executors.newSingleThreadScheduledExecutor();
        this.timer.scheduleAtFixedRate(frameGrabber, 0, 20, TimeUnit.MILLISECONDS);
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
        contourConcatImgLbl.setIcon(new ImageIcon(imgToSet));
    }


    public BufferedImage buffImageFromObjectList(List<ComposedObject> composedObjectList, Mat matImage) throws IOException {
        Mat drawing = Mat.zeros(matImage.size(), CvType.CV_8UC3);
//        Mat drawing = Mat.zeros(new Size(304, 322), CvType.CV_8UC3);


        for (int i = 0; i < composedObjectList.size(); i++) {
            Scalar color = composedObjectList.get(i).getCurrentFrameIndividualObject().getColor();
            Imgproc.rectangle(drawing,
                    composedObjectList.get(i).getCurrentFrameIndividualObject().getBoundRect().tl(),
                    composedObjectList.get(i).getCurrentFrameIndividualObject().getBoundRect().br(), color, 1);
        }

        BufferedImage bufferedImage = MatBufferedImageConverter.Mat2BufferedImage(drawing);

        return bufferedImage;
    }
}
