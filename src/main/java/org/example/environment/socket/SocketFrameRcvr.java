package org.example.environment.socket;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SocketFrameRcvr {
    int port = 1025;
    Socket socket;

    public SocketFrameRcvr() throws IOException {
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
