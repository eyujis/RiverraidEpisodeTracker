package org.example.util;

import java.awt.image.BufferedImage;

public class RawEnvInput {
    public BufferedImage bufferedImage;
    public double reward;
    public boolean terminal;

    public RawEnvInput(BufferedImage bufferedImage, double reward, boolean terminal) {
        this.bufferedImage = bufferedImage;
        this.reward = reward;
        this.terminal = terminal;
    }
}
