package org.example.socket;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SocketImgRcvr {
    int port = 1025;
    Socket socket;

    public SocketImgRcvr() throws IOException {
        socket = new Socket("localhost", port);
    }

    public BufferedImage receiveImage() throws IOException {
        InputStream inputStream = socket.getInputStream();
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        inputStream.close();
        socket.close();
        return bufferedImage;
    }
}
