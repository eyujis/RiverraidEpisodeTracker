package org.example.visualization;

import org.example.environment.socket.SocketFrameRcvr;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ButtonStartRcvImgs implements ActionListener {

    private JLabel rawDataBufferImg;
    private ScheduledExecutorService timer;

    public ButtonStartRcvImgs(JLabel rawDataBufferImg) throws IOException {
        this.rawDataBufferImg = rawDataBufferImg;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Runnable frameGrabber = new Runnable() {
            @Override public void run() {
                try {
                    BufferedImage imgFromSocket = new SocketFrameRcvr().receiveImage();
                    setImgToSocketImgLbl(imgFromSocket);
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
        rawDataBufferImg.setIcon(new ImageIcon(imgToSet));
    }

}
