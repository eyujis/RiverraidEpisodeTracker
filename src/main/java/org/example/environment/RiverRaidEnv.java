package org.example.environment;

import org.example.util.RawEnvInput;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface RiverRaidEnv {
    public RawEnvInput step();

    public void communicateAction(int action);

    public int getNStep();
}
