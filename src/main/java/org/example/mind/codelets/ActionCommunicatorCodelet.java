package org.example.mind.codelets;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import org.example.environment.RiverRaidEnv;

import java.util.Arrays;

public class ActionCommunicatorCodelet extends Codelet {
    private Memory actionTimestampMO;

    private RiverRaidEnv env;
    private int timestamp = 0;

    public ActionCommunicatorCodelet(RiverRaidEnv env) {
        this.env = env;
    }

    @Override
    public void accessMemoryObjects() {
        actionTimestampMO = getOutput("ACTION_TIMESTAMP");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        env.communicateAction(1);

        timestamp += 1;
        actionTimestampMO.setI(timestamp);
    }
}
