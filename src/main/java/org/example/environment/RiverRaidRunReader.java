package org.example.environment;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RiverRaidRunReader implements RiverRaidEnv {
    int nStep = 0;
    BufferedImage image;

    public RiverRaidRunReader() throws IOException {
    }

    public BufferedImage step() {
        String filePath = "src/main/datasets/dataset_1/" + nStep + ".tiff";

        try {
            File file = new File(filePath);

            image = ImageIO.read(file);

            if (image != null) {
            } else {
                System.out.println("Failed to read TIFF image.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        nStep++;
        return  image;
    }

    public int getNStep() {
        return nStep;
    }
}
