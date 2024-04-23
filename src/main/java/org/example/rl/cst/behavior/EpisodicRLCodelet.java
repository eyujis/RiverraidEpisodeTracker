package org.example.rl.cst.behavior;

import br.unicamp.cst.core.entities.MemoryObject;
import org.example.rl.cst.behavior.RL.actionSpaces.ActionSpace;
import org.example.rl.cst.behavior.RL.learners.RLLearner;

import java.util.ArrayList;

public class EpisodicRLCodelet extends RLCodelet {

    private final int episodesPerSave = 2;
    protected int episodeCounter = 0;

    public EpisodicRLCodelet(RLLearner learner, ActionSpace actionSpace, MemoryObject perceptMO) {
        super(learner, actionSpace, perceptMO);
    }

    @Override
    public void endStep(boolean terminal) {
        if (terminal) {
            episodeCounter += 1;
            addGraphDataPoint(Integer.toString(episodeCounter));
            if (episodeCounter % episodesPerSave == 0) {
                saveGraphData();
            }

            stepCounter = 0;
            cumulativeReward = 0;
            learner.endEpisode();

            trial = new ArrayList<>();
        }
    }
}
