package org.example.mind.codelets.event_tracker;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;

public class EventTrackerCodelet extends Codelet {
    Memory detectedObjectsMO;
    Memory eventCategoriesMO;
    Memory detectedEventsMO;

    @Override
    public void accessMemoryObjects() {
        detectedObjectsMO=(MemoryObject)this.getInput("DETECTED_OBJECTS");
        eventCategoriesMO=(MemoryObject)this.getInput("EVENT_CATEGORIES");
        detectedObjectsMO=(MemoryObject)this.getInput("DETECTED_EVENTS");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {

    }
}
