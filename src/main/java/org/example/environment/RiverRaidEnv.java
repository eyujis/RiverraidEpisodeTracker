package org.example.environment;

import org.example.environment.socket.SocketFrameRcvr;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class RiverRaidEnv {
    private SocketFrameRcvr socketFrameRcvr = new SocketFrameRcvr();
    int nStep = 0;

    public RiverRaidEnv() throws IOException {
    }
    
    public BufferedImage step() throws IOException {
        nStep++;
        return new SocketFrameRcvr().receiveImage();
    }

    public int getNStep() {
        return nStep;
    }
}
