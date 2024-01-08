package org.example.mind.codelets.event_cat_learner;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;

public class EventCategoryLearnerCodelet extends Codelet {
    Memory objectsBufferMO;
    Memory eventCategoriesMO;
    EventCategoryLearner eventCategoryLearner = new EventCategoryLearner();

    @Override
    public void accessMemoryObjects() {
        objectsBufferMO =(MemoryObject)this.getInput("OBJECTS_BUFFER");
        eventCategoriesMO =(MemoryObject)this.getOutput("EVENT_CATEGORIES");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        synchronized (eventCategoriesMO) {
            Idea eventCategories = null;

            if(eventCategoriesMO.getI()=="") {
                eventCategories = new Idea("EventCategories", "", 0);
            } else {
                eventCategories = (Idea) eventCategoriesMO.getI();
            }
            System.out.println(eventCategories.getL().size());
            eventCategoryLearner.updateCategories((Idea) objectsBufferMO.getI(), eventCategories);
            System.out.println(eventCategoryLearner.getRelevantCategories().getL().size());
            System.out.println("====================");
            eventCategoriesMO.setI(eventCategoryLearner.getRelevantCategories());
        }
    }
}
