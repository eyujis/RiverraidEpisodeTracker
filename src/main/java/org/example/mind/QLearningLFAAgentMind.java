package org.example.mind;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import org.example.environment.RiverRaidEnv;
import org.example.rl.cst.behavior.EpisodicRLCodelet;
import org.example.rl.cst.behavior.RL.actionSpaces.ActionSpace;
import org.example.rl.cst.behavior.RL.actionSpaces.DiscreteActionSpace;
import org.example.rl.cst.behavior.RL.featureExtractors.DirectFeatureExtractor;
import org.example.rl.cst.behavior.RL.featureExtractors.FeatureExtractor;
import org.example.rl.cst.behavior.RL.featureExtractors.LCFeatureExtractor;
import org.example.rl.cst.behavior.RL.featureExtractors.NormalizingFeatureExtractor;
import org.example.rl.cst.behavior.RL.learners.LFAQLearning;
import org.example.visualization.FirstJFrame;
import org.example.visualization.SecondJFrame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QLearningLFAAgentMind extends AgentMind {
    final double initialAlpha = 0.001;
    final double initialEpsilon = 0.9;
    final int episodesToConverge = 4000;
    final double gamma = 0.9;

    public QLearningLFAAgentMind(RiverRaidEnv env, FirstJFrame firstJFrame, SecondJFrame secondJFrame) throws IOException {
        super(env, firstJFrame, secondJFrame);
    }

    @Override
    protected Codelet getRLCodelet(Memory rlPerceptMO) {
        ArrayList<ArrayList<Double>> actions = new ArrayList<ArrayList<Double>>() {
            {
                add(new ArrayList<Double>(List.of(0.0)));
                add(new ArrayList<Double>(List.of(1.0)));
                add(new ArrayList<Double>(List.of(2.0)));
            }
        };
        ActionSpace actionSpace = new DiscreteActionSpace(actions);
        //FeatureExtractor featureExtractor = new LCFeatureExtractor(1);
        FeatureExtractor featureExtractor = new NormalizingFeatureExtractor();
        featureExtractor.setMaxStateValues(new ArrayList<Double>(Arrays.asList(320.0, 16.0, 420.0, 320.0, 16.0, 420.0, 320.0, 16.0, 420.0, 320.0, 16.0, 420.0, 320.0, 16.0, 420.0, 320.0, 16.0, 420.0, 320.0, 16.0, 420.0, 320.0, 16.0, 420.0, 320.0, 16.0, 420.0, 320.0, 16.0, 420.0, 320.0, 16.0, 420.0, 320.0, 16.0, 420.0, 320.0, 16.0))); // TODO: Change velocity max
        LFAQLearning lfaqLearning = new LFAQLearning(initialAlpha, initialEpsilon, episodesToConverge, gamma, featureExtractor);

        return new EpisodicRLCodelet(lfaqLearning, actionSpace, (MemoryObject) rlPerceptMO);
    }
}
