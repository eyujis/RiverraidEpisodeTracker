package org.example.mind.codelets;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import org.example.environment.RiverRaidEnv;

import java.util.ArrayList;
import java.util.Arrays;

public class ActionCommunicatorCodelet extends Codelet {
    private Memory rlActionMO;
    private Memory actionTimestampMO;

    private RiverRaidEnv env;
    private int timestamp = 0;

    public ActionCommunicatorCodelet(RiverRaidEnv env) {
        this.env = env;
    }

    @Override
    public void accessMemoryObjects() {
        rlActionMO = getInput("RLACTION");
        actionTimestampMO = getOutput("ACTION_TIMESTAMP");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        env.communicateAction(((ArrayList<Double>) rlActionMO.getI()).get(0).intValue());

        timestamp += 1;
        actionTimestampMO.setI(timestamp);
    }
}
