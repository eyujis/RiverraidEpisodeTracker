package org.example.rl.cst.behavior.RL.learners;

import org.example.rl.util.RLPercept;

import java.util.ArrayList;
import java.util.List;

public class TorchBringerLearner extends RLLearner {
    private String configName;


    private ArrayList<Double> pastAction = null;
    private final TorchBringerClient client;

    public TorchBringerLearner(TorchBringerClient client, String configPath) {
        this.configName = configPath.substring(configPath.lastIndexOf("\\") + 1);
        this.client = client;

        client.initialize(configPath);
    }

    @Override
    public void rlStep(ArrayList<RLPercept> trial) {
        this.pastAction = client.step(trial.get(trial.size() - 1));
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
