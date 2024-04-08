package org.example.dataset_generator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class DatasetGenerator {
    int datasetSize = 10;
    int port = 1025;

    public void run() throws IOException, InterruptedException {
        int frameCount = 0;
        File dir = createAvailableDirectoryPath();

        if(dir == null) {
            return;
        }

        while(true) {
            Socket socket = new Socket("localhost", port);;
            BufferedImage bufferedImage = receiveImage(socket);
            socket.close();

            String outputFilePath = dir.getPath()+"/"+frameCount+".tiff";
            File outputFile = new File(outputFilePath);
            ImageIO.write(bufferedImage, "tiff", outputFile);
            System.out.println(frameCount);
            frameCount ++;

            Thread.sleep(200);
        }
    }

    private BufferedImage receiveImage(Socket socket) {
        InputStream inputStream = null;
        BufferedImage bufferedImage = null;
        try {
            inputStream = socket.getInputStream();
            bufferedImage = ImageIO.read(inputStream);
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bufferedImage;
    }

    private File createAvailableDirectoryPath() {
        int idx = 0;
        File directory = null;

        while(idx < datasetSize) {
            String directoryPath = "src/main/datasets/dataset_"+idx;
            directory = new File(directoryPath);

            if(!directory.exists()) {
                directory.mkdir();
                return directory;
            }
            idx++;
        }

        return null;
    }
}
