package org.example;

import org.example.environment.RiverRaidEnv;
import org.example.environment.RiverRaidPyGame;
import org.example.mind.AgentMind;
import org.example.mind.QLearningLFAAgentMind;
import org.example.visualization.FirstJFrame;
import org.example.visualization.SecondJFrame;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        loadOpenCVLibraryFromCurrentPath();
        boolean IS_PRODUCTION = false;

        FirstJFrame firstJFrame = null;
        SecondJFrame secondJFrame = null;

        if(!IS_PRODUCTION) {
            firstJFrame = new FirstJFrame();
            secondJFrame = new SecondJFrame();

            firstJFrame.setVisible(true);
            secondJFrame.setVisible(true);
        }

        RiverRaidEnv riverRaidEnv = new RiverRaidPyGame();
        AgentMind agentMind = new QLearningLFAAgentMind(riverRaidEnv, firstJFrame, secondJFrame);
    }

    public static void loadOpenCVLibraryFromCurrentPath()   {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.load(s+"\\libs\\OpenCVWindows\\java\\x64\\opencv_java455.dll");
    }
}