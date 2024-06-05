package org.example.mind;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import org.example.environment.RiverRaidEnv;
import org.example.rl.cst.behavior.EpisodicRLCodelet;
import org.example.rl.cst.behavior.RL.actionSpaces.ActionSpace;
import org.example.rl.cst.behavior.RL.actionSpaces.DiscreteActionSpace;
import org.example.rl.cst.behavior.RL.learners.TensorforceLearner;
import org.example.rl.cst.behavior.RL.learners.TorchBringerLearner;
import org.example.visualization.FirstJFrame;
import org.example.visualization.SecondJFrame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TorchBringerAgentMind extends AgentMind {
    final String configPath = "C:\\Users\\morai\\OneDrive\\Documentos\\Git\\RiverraidEpisodeTracker\\src\\main\\java\\org\\example\\rl\\cst\\behavior\\RL\\configs\\torchbringer\\freeway.json";

    public TorchBringerAgentMind(RiverRaidEnv env, FirstJFrame firstJFrame, SecondJFrame secondJFrame) throws IOException {
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

        TorchBringerLearner torchBringerLearner;
        torchBringerLearner = new TorchBringerLearner(50051, configPath);

        return new EpisodicRLCodelet(torchBringerLearner, actionSpace, (MemoryObject) rlPerceptMO);
    }
}
