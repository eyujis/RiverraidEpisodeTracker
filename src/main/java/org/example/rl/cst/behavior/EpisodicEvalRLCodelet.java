package org.example.rl.cst.behavior;

import br.unicamp.cst.core.entities.MemoryObject;
import org.example.rl.cst.behavior.RL.actionSpaces.ActionSpace;
import org.example.rl.cst.behavior.RL.learners.EvalRLLearner;
import org.example.rl.cst.behavior.RL.learners.RLLearner;

import java.util.ArrayList;

public class EpisodicEvalRLCodelet extends EpisodicRLCodelet {
    private final int evalEpisodes;
    private final int evalInterval;

    private int evalCounter = 0;
    private boolean isEval = false;

    public EpisodicEvalRLCodelet(EvalRLLearner learner, ActionSpace actionSpace, MemoryObject perceptMO, int evalEpisodes, int evalInterval) {
        super(learner, actionSpace, perceptMO);

        this.evalEpisodes = evalEpisodes;
        this.evalInterval = evalInterval;
    }

    @Override
    protected void callStep() {
        if (isEval) {
            ((EvalRLLearner) learner).rlEval(trial);
        } else {
            learner.rlStep(trial);
        }
    }

    @Override
    public void endStep(boolean terminal) {
        if (terminal) {
            if (isEval) { // Run eval episodes until eval counter hits evalEpisodes
                evalCounter++;

                if (evalCounter >= evalEpisodes) {
                    cumulativeReward /= evalEpisodes;

                    addGraphDataPoint(Integer.toString(episodeCounter));
                    saveGraphData();

                    evalCounter = 0;
                    isEval = false;
                }
            } else { // Run step episodes until episode counter is multiple of evalInterval
                if ((episodeCounter + 1) % evalInterval == 0) {
                    isEval = true;
                }

                episodeCounter += 1;
                cumulativeReward = 0;
            }

            stepCounter = 0;
            learner.endEpisode();
            trial = new ArrayList<>();
        }
    }
}
