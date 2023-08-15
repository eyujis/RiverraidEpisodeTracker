package org.example.mind.codelets.event_tracker;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;

public class EventTrackerCodelet extends Codelet {
    Memory objectsBufferMO;
    Memory eventCategoriesMO;
    Memory detectedEventsMO;

    EventTracker eventTracker = new EventTracker();

    @Override
    public void accessMemoryObjects() {
        objectsBufferMO=(MemoryObject)this.getInput("OBJECTS_BUFFER");
        eventCategoriesMO=(MemoryObject)this.getInput("EVENT_CATEGORIES");
        detectedEventsMO=(MemoryObject)this.getOutput("DETECTED_EVENTS");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        if(objectsBufferMO.getI()=="" || eventCategoriesMO.getI()=="") {
            return;
        }
        Idea objectsBuffer = (Idea) objectsBufferMO.getI();
        Idea eventCategories = (Idea) eventCategoriesMO.getI();
        eventTracker.detectEvents(objectsBuffer, eventCategories);

//        System.out.println(eventTracker.getDetectedEvents().toStringFull());

        for(Idea eventIdea: eventTracker.getDetectedEvents().getL()) {
            if((boolean)eventIdea.get("hasEnded").getValue()==true) {
                System.out.println(eventIdea.toStringFull());
            }
        }

        detectedEventsMO.setI(eventTracker.getDetectedEvents());
    }
}
