package org.example.rl.cst.behavior.RL.learners;

import org.example.rl.util.RLPercept;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TorchBringerLearner extends RLLearner {
    private String configName;

    private TorchBringerClient torchBringerClient;

    private ArrayList<Double> pastAction = null;

    public TorchBringerLearner(int port, String configPath) {
        this.configName = configPath.substring(configPath.lastIndexOf("\\") + 1);

        torchBringerClient = new TorchBringerClient(port);
        torchBringerClient.initialize(configPath);
    }

    @Override
    public void rlStep(ArrayList<RLPercept> trial) {
        this.pastAction = torchBringerClient.step(trial.get(trial.size() - 1));
    }

    @Override
    public ArrayList<Double> selectAction(ArrayList<Double> s) {
        if (pastAction == null) {
            return new ArrayList<>(List.of(0.0));
        }
        return this.pastAction;
    }

    @Override
    public void endEpisode() {

    }

    @Override
    public String toString() {
        return "TorchbringerLearner(configPath=" + configName + ")";
    }
}
