package org.example.view;

import org.example.objectDetection.ContourDetector;
import org.example.socket.SocketImgRcvr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ButtonStartRcvImg implements ActionListener {

    private JLabel socketImgLbl;
    private JLabel contourImgLbl;
    private ScheduledExecutorService timer;

    private SocketImgRcvr socketImgRcvr;

    private ContourDetector contourDetector;

    public ButtonStartRcvImg(JLabel socketImgLbl, JLabel contourImgLbl) throws IOException {
        this.socketImgLbl = socketImgLbl;
        this.contourImgLbl = contourImgLbl;
        contourDetector = new ContourDetector();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Runnable frameGrabber = new Runnable() {
            @Override public void run() {
                try {
                    BufferedImage imgFromSocket = new SocketImgRcvr().receiveImage();
                    setImgToSocketImgLbl(imgFromSocket);
                    setImgToContourImgLbl(contourDetector.addContours(imgFromSocket));
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
}
