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
//    static int imageId = 0;

    public SocketFrameRcvr() throws IOException {
        socket = new Socket("localhost", port);
    }

    public BufferedImage receiveImage() throws IOException {
        InputStream inputStream = socket.getInputStream();
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        inputStream.close();
        socket.close();

//
//        String filePath = "src/main/datasets/dataset_0/"+imageId+".tiff";
//        File outputFile = new File(filePath);
//        ImageIO.write(bufferedImage, "tiff", outputFile);

//        imageId++;

        return bufferedImage;
    }
}
