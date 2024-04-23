package org.example.rl.cst.behavior.RL.learners;

import org.example.rl.util.RLPercept;

import java.util.ArrayList;

public abstract class EvalRLLearner extends RLLearner {
    abstract public void rlEval(ArrayList<RLPercept> trial);
}
