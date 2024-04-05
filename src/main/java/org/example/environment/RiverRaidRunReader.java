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

    public Observation step() {
        Observation observation = new Observation();
        observation.done = false;

        String filePath = "src/main/datasets/dataset_0/" + nStep + ".tiff";
        File file = new File(filePath);

        if(!file.exists() || nStep==20) {
            observation.done = true;
            return observation;
        }

        try {
            image = ImageIO.read(file);
            observation.image = image;
        } catch (IOException e) {
            e.printStackTrace();
        }

        observation.timestamp = nStep;
        nStep++;

        return  observation;
    }
}
