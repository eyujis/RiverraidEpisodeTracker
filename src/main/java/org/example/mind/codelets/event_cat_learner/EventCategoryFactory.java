package org.example.mind.codelets.event_cat_learner;

import br.unicamp.cst.representation.idea.Idea;

public class EventCategoryFactory {
    static int factoryId = 0;

    public Idea createVectorEventCategory(String propertyName, Idea objectTransition, double relevance) {
        Idea category = new Idea("VectorEventCategory"+ generateEventId(), new VectorEventCategory(propertyName, objectTransition,
                relevance));
        return category;
    }

    public Idea createAppearanceEventCategory(Idea objectTransition, double relevance) {
        Idea category = new Idea("AppearanceEventCategory"+ generateEventId(), new AppearanceEventCategory(objectTransition,
                relevance));
        return category;
    }

    public int generateEventId() {
        factoryId++;
        return factoryId;
    }
}
