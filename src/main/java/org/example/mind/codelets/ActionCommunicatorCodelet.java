package org.example.mind.codelets;

import br.unicamp.cst.core.entities.Codelet;
import org.example.environment.RiverRaidEnv;

import java.util.Arrays;

public class ActionCommunicatorCodelet extends Codelet {
    private Codelet nextProc;
    private RiverRaidEnv env;

    public ActionCommunicatorCodelet(RiverRaidEnv env, Codelet nextProc) {
        this.env = env;
        this.nextProc = nextProc;
    }

    @Override
    public void accessMemoryObjects() {

    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        env.communicateAction(1);

        nextProc.accessMemoryObjects();
        nextProc.proc();
    }
}
