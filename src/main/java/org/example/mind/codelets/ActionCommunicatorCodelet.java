package org.example.mind.codelets;

import br.unicamp.cst.core.entities.Codelet;
import org.example.environment.RiverRaidEnv;

import java.util.Arrays;

public class ActionCommunicatorCodelet extends Codelet {
    private RiverRaidEnv env;

    public ActionCommunicatorCodelet(RiverRaidEnv env) {
        this.env = env;
    }

    @Override
    public void accessMemoryObjects() {

    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        env.communicateAction(Arrays.asList(2));
    }
}
