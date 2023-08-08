package org.example.mind.codelets.event_cat_learner;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_cat_learner.entities.FragmentCategory;
import org.example.mind.codelets.object_cat_learner.entities.ObjectCategory;

import java.util.ArrayList;

public class EventCategoryFactory {
    static int factoryId = 0;

    public Idea createEventCategory(String propertyName, Idea objectTransition, double relevance) {
        Idea category = new Idea("EventCategory"+ generateEventId(), new EventCategory(propertyName, objectTransition,
                relevance));
        return category;
    }


    public int generateEventId() {
        factoryId++;
        return factoryId;
    }
}
