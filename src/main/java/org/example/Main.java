package org.example;

import org.example.environment.RiverRaidRunReader;
import org.example.environment.RiverRaidEnv;
import org.example.mind.AgentMind;
import org.example.results_writer.ResultsFileWriter;
import org.example.visualization.FirstJFrame;
import org.example.visualization.SecondJFrame;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.System.getProperty;

public class Main {
    public static void main(String[] args) throws IOException {
        loadOpenCVLibraryFromCurrentPath();

        int runNumber = Integer.parseInt(args[0]);

        if(runNumber == 0) {
            new ResultsFileWriter().createHowManyResultsFile();
            new ResultsFileWriter().createWhichDestroyedResultsFile();
            new ResultsFileWriter().createWhenSecondFuelResultsFile();
            new ResultsFileWriter().createWhenBridgeTargetResultsFile();
        }

        FirstJFrame firstJFrame = new FirstJFrame();
        SecondJFrame secondJFrame = new SecondJFrame();

        firstJFrame.setVisible(true);
        secondJFrame.setVisible(true);

        RiverRaidEnv riverRaidEnv = new RiverRaidRunReader(runNumber);
        AgentMind agentMind = new AgentMind(riverRaidEnv, firstJFrame, secondJFrame);
    }

    public static void loadOpenCVLibraryFromCurrentPath()   {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.load(s+"/libs/libopencv_java455.so");
    }
}