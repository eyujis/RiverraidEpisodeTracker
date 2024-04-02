package org.example.mind.codelets.event_cat_learner;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.event_cat_learner.entities.ObjectsTransitionsExtractor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class EventCategoryLearner {
    Idea eventCategories;
    EventCategoryFactory eventCatFactory;
    ObjectsTransitionsExtractor objectsTransitionsExtractor;

    double RELEVANCE_THRESHOLD = 5;
    double INCREMENT_FACTOR = 2.2;
    double DECREMENT_FACTOR = 0.5;
    double MINIMUM_RELEVANCE = 0.5;

    public EventCategoryLearner() {
        eventCatFactory = new EventCategoryFactory();
        eventCategories = new Idea("EventCategories", "", 0);
        objectsTransitionsExtractor = new ObjectsTransitionsExtractor();
    }

    public void updateCategories(Idea objectsBuffer, Idea eventCategories) {
        this.eventCategories = eventCategories;

        Idea objectsTransitions = objectsTransitionsExtractor.extract(objectsBuffer);

        // Remember categories based on membership
        for (Idea objectTransition : objectsTransitions.getL()) {
            for (Idea eventCategoryIdea : eventCategories.getL()) {
                EventCategory eventCategory = (EventCategory) eventCategoryIdea.getValue();
                double membership = eventCategory.membership(objectTransition);
                if(membership==1) {
                    if (eventCategory.getRelevance()<RELEVANCE_THRESHOLD) {
                        eventCategory.incrementRelevance(INCREMENT_FACTOR);
                    }
                    break;
                }
            }
        }
        decrementCategoriesRelevance();
        removeIrrelevantCategories();
    }

    public int equalCategoryIdx(Idea categoryFromInstance) {
        int idx = -1;

        for(int i=0; i<eventCategories.getL().size(); i++) {
            EventCategory eventCatListElem = (EventCategory) eventCategories.getL().get(i).getValue();
            EventCategory eventInstance = (EventCategory) categoryFromInstance.getValue();

            if(eventCatListElem.sameCategory(eventInstance)) {
                idx = i;
            }
        }

        return idx;
    }

    public void decrementCategoriesRelevance() {
        for(Idea eventCategoryIdea: eventCategories.getL()) {
            EventCategory eventCategory = (EventCategory) eventCategoryIdea.getValue();
            if(eventCategory.getRelevance() < RELEVANCE_THRESHOLD) {
                eventCategory.decrementRelevance(DECREMENT_FACTOR);
            }
        }
    }

    public void removeIrrelevantCategories() {
        ArrayList<Integer> idxsToRemove = new ArrayList();

        for (int i=0; i<eventCategories.getL().size(); i++) {
            Idea eventCatIdea = eventCategories.getL().get(i);
            EventCategory eventCat = (EventCategory) eventCatIdea.getValue();
            if (eventCat.getRelevance() < MINIMUM_RELEVANCE) {
                idxsToRemove.add(i);
            }
        }

        removeIdxFromObjectCategoryList(idxsToRemove);
    }

    private void removeIdxFromObjectCategoryList(ArrayList<Integer> idxsToRemove) {
        Collections.sort(idxsToRemove, Collections.reverseOrder());

        for (int index : idxsToRemove) {
            if (index >= 0 && index < eventCategories.getL().size()) {
                eventCategories.getL().remove(index);
            }
        }
    }

    public Idea getRelevantCategories() {
        Idea relevantCategories = new Idea("RelevantCategories", "", 0);;

        for (Idea eventCatIdea : eventCategories.getL()) {
            EventCategory eventCategory = (EventCategory) eventCatIdea.getValue();
            if (eventCategory.getRelevance()>=RELEVANCE_THRESHOLD) {
                relevantCategories.getL().add(eventCatIdea);
            }
        }
        return relevantCategories;
    }
    public String findEventType(Idea objectTransition) {
        List<Idea> timeSteps = objectTransition.get("timeSteps").getL();

        for (int i = 0; i < timeSteps.size(); i++) {
            if (timeSteps.get(i).get("idObject") == null) {
                return "AppearanceEventCategory";
            }
        }
        return "VectorEventCategory";
    }
}
