package org.example;

import org.example.environment.RiverRaidEnv;
import org.example.mind.AgentMind;
import org.example.view.PerceptJFrame;
import org.example.visualization.MemoriesJFrame;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        loadOpenCVLibraryFromCurrentPath();

//        PerceptJFrame perceptJFrame = new PerceptJFrame();
//        perceptJFrame.setVisible(true);

        MemoriesJFrame memoriesJFrame = new MemoriesJFrame();
        memoriesJFrame.setVisible(true);
        RiverRaidEnv riverRaidEnv = new RiverRaidEnv();
        AgentMind agentMind = new AgentMind(riverRaidEnv,
                                            memoriesJFrame.getRawDataBufferImgJLabel(),
                                            memoriesJFrame.getObjectsImgJLabel(),
                                            memoriesJFrame.getMergedObjectsImgJLabel());
    }

    public static void loadOpenCVLibraryFromCurrentPath()   {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.load(s+"/libs/libopencv_java455.so");
    }
}