package org.example.environment;

import org.example.environment.socket.SocketFrameCommunicator;
import org.example.util.RawEnvInput;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RiverRaidPyGame implements RiverRaidEnv {
    int nStep = 0;
    BufferedImage rcvImage;
    double reward;
    boolean terminal;

    public RiverRaidPyGame() throws IOException {
    }
    
    public RawEnvInput step()  {
        try {
            rcvImage = new SocketFrameCommunicator().receiveImage();
            reward = new SocketFrameCommunicator().receiveReward();
            terminal = new SocketFrameCommunicator().receiveTerminal();
        } catch (IOException e) {
            e.printStackTrace();
        }

        nStep++;
        return new RawEnvInput(rcvImage, reward, terminal);
    }

    @Override
    public void communicateAction(int action) {
        try {
            new SocketFrameCommunicator().sendAction(action);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNStep() {
        return nStep;
    }
}
