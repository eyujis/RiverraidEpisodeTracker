package org.example.mind.codelets.rl_communication;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import org.example.rl.util.RLPercept;

import java.util.ArrayList;

public class RLPerceptCreatorCodelet extends Codelet {
    private final int MAX_CARS = 12;

    private Memory detectedEventsMO;

    private Memory rewardBufferMO;
    private Memory terminalBufferMO;
    private Memory RLPerceptMO;


    @Override
    public void accessMemoryObjects() {
        detectedEventsMO = (MemoryObject) this.getInput("DETECTED_EVENTS");
        rewardBufferMO=(MemoryObject)this.getInput("REWARD_BUFFER");
        terminalBufferMO=(MemoryObject)this.getInput("TERMINAL_BUFFER");
        RLPerceptMO = (MemoryObject) this.getOutput("RLPERCEPT");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        Idea detectedEvents;
        if (detectedEventsMO.getI() == "") {
            detectedEvents = new Idea();
        } else {
            detectedEvents = (Idea) detectedEventsMO.getI();
        }

        ArrayList<Double> state = new ArrayList<>();
        state.add(0.0); // Player y
        state.add(0.0); // Player y speed
        for (int i = 0; i < MAX_CARS; i++) {
            for (int j = 0; j < 3; j ++) {
                state.add(0.0); // x pos, y pos and x speed for every car
            }
        }

        int carPos = 2;
        for (Idea event : detectedEvents.getL()) {
            if (((String) event.getL().get(3).getValue()).startsWith("Vector")) { // If is vector type event
                if ((boolean) event.getL().get(1).getL().get(5).getValue()) { // If is agent
                    if ((double) event.getL().get(1).getL().get(1).getL().get(0).getValue() < 100.0) { // If is playable agent
                        state.set(0, (double) event.getL().get(1).getL().get(1).getL().get(1).getValue()); // Adds y
                        state.set(1, extractEventVelocity(event, 1)); // Adds y speed
                    }
                } else if ((carPos - 2) / 3 < MAX_CARS) { // If is car and has space
                    state.set(carPos, (double) event.getL().get(0).getL().get(0).getValue()); // Adds x
                    state.set(carPos + 1, (double) event.getL().get(0).getL().get(1).getValue()); // Adds y
                    state.set(carPos + 2, extractEventVelocity(event, 0)); // Adds x speed
                    carPos += 3;
                }
            }
        }

        RLPercept percept = new RLPercept(state, (Double) rewardBufferMO.getI(), (boolean) terminalBufferMO.getI());
        RLPerceptMO.setI(percept);
    }

    private Double extractEventVelocity(Idea event, int axis) {
        return ((double) event.getL().get(0).getL().get(axis).getValue() - // initialPropertyState.axis
                (double) event.getL().get(1).getL().get(1).getL().get(axis).getValue()) / // lastObjectState.center.axis
                (double) ((int) event.getL().get(2).getValue() - // currentTimestamp.value
                        (int) event.getL().get(7).getValue()); // initialTimestamp.value
    }
}
