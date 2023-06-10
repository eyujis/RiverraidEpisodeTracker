package org.example.util;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MatBufferedImageConverter {
    public static Mat BufferedImage2Mat(BufferedImage image) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "tiff", byteArrayOutputStream);
            byteArrayOutputStream.flush();
            return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
        } catch(Exception err) {
            System.out.println(err.getMessage());
        }
        return null;
    }
    public static BufferedImage Mat2BufferedImage(Mat matrix)throws IOException {
        try {
            MatOfByte mob=new MatOfByte();
            Imgcodecs.imencode(".tiff", matrix, mob);
            return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
        } catch(Exception err) {
            System.out.println(err.getMessage());
        }
        return null;
    }
}
