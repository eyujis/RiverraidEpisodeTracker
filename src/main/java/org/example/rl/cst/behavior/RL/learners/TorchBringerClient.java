package org.example.rl.cst.behavior.RL.learners;

import org.example.rl.util.RLPercept;

import java.util.ArrayList;

public abstract class TorchBringerClient {
    abstract public void initialize(String configPath);
    abstract public ArrayList<Double> step(RLPercept percept);
}
