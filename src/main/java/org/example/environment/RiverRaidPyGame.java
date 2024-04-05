package org.example.environment;

import org.example.environment.socket.SocketFrameRcvr;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class RiverRaidPyGame implements RiverRaidEnv {
    private SocketFrameRcvr socketFrameRcvr = new SocketFrameRcvr();
    int nStep = 0;
    BufferedImage rcvImage;

    public RiverRaidPyGame() throws IOException {
    }
    
    public Observation step()  {
        Observation observation = new Observation();
        observation.done = false;

        try {
            observation.image = new SocketFrameRcvr().receiveImage();
        } catch (IOException e) {
            observation.done = true;
            e.printStackTrace();
        }
        observation.timestamp = nStep;
        nStep++;
        return observation;
    }
}
