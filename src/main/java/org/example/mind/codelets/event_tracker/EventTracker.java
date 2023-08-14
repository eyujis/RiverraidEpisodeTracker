package org.example.mind.codelets.event_tracker;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.event_cat_learner.EventCategory;
import org.example.mind.codelets.event_cat_learner.entities.ObjectsTransitionsExtractor;

public class EventTracker {
    Idea detectedEvents;
    ObjectsTransitionsExtractor objectsTransitionsExtractor;

    public EventTracker() {
        detectedEvents = new Idea("DetectedEvents", "", 0);
        objectsTransitionsExtractor = new ObjectsTransitionsExtractor();
    }

    public void detectEvents(Idea objectsBuffer, Idea eventCategories) {
        Idea objectTransitions = objectsTransitionsExtractor.extract(objectsBuffer);

        for(Idea objectTransition: objectTransitions.getL()) {
            for(Idea eventCategory: eventCategories.getL()) {
                double membership = ((EventCategory) eventCategory.getValue()).membership(objectTransition);
                if(membership==1) {

                }

            }
        }
    }

    public int getEventIdx(String eventCategoryName) {
        for(int i=0; i<detectedEvents.getL().size(); i++) {
            if(eventCategoryName == detectedEvents.getL().get(i).get("category").getValue()) {
                return i;
            }
        }
        return -1;
    }
}
