package org.example.environment;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface RiverRaidEnv {
    public BufferedImage step();

    public int getNStep();
}
