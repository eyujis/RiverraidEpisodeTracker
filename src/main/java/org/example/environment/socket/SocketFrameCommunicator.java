package org.example.environment.socket;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class SocketFrameCommunicator {
    int port = 1026;
    Socket socket;
//    static int imageId = 0;

    public SocketFrameCommunicator() throws IOException {
        socket = new Socket("localhost", port);
    }

    public BufferedImage receiveImage() throws IOException {
        InputStream inputStream = socket.getInputStream();
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        inputStream.close();

        //JFrame frame = new JFrame();
        //frame.getContentPane().setLayout(new FlowLayout());
        //frame.getContentPane().add(new JLabel(new ImageIcon(bufferedImage)));
        //frame.pack();
        //frame.setVisible(true);

        socket.close();

        //TODO automatically create datasets when playing
//        String filePath = "src/main/datasets/dataset_0/"+imageId+".tiff";
//        File outputFile = new File(filePath);
//        ImageIO.write(bufferedImage, "tiff", outputFile);

//        imageId++;
        return bufferedImage;
    }

    public float receiveReward() throws IOException {
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        float reward = Float.parseFloat(reader.readLine());
        inputStream.close();
        socket.close();

        return reward;
    }

    public boolean receiveTerminal() throws IOException {
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        boolean terminal = inputStream.readBoolean();
        inputStream.close();
        socket.close();

        return terminal;
    }

    public void sendAction(int action) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        dataOutputStream.writeInt(action);

        socket.close();
    }
}
