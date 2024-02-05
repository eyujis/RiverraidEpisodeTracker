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
    
    public BufferedImage step()  {
        try {
            rcvImage = new SocketFrameRcvr().receiveImage();
        } catch (IOException e) {
            e.printStackTrace();
        }

        nStep++;
        return rcvImage;
    }

    public int getNStep() {
        return nStep;
    }
}
