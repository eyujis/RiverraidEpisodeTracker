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
//    double INIT_RELEVANCE = 1;
    double INIT_RELEVANCE = 5;
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

        // Remember categories based on category similarity
//        Idea rcvEventCats = extractEventCategories(objectsTransitions);
//
//        for (Idea rcvCat : rcvEventCats.getL()) {
//            int equalCatIdx = this.equalCategoryIdx(rcvCat);
//
//            if (equalCatIdx != -1) {
//                EventCategory eventCat = (EventCategory) eventCategories.getL().get(equalCatIdx).getValue();
//                if (eventCat.getRelevance()<RELEVANCE_THRESHOLD) {
//                    eventCat.incrementRelevance(INCREMENT_FACTOR);
//                }
//            }
//        }

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

    public Idea extractEventCategories(Idea objectsTransitions) {
        Idea rcvEventCats = new Idea("extractedEventCategories", "", 0);
        for (Idea objectTransition: objectsTransitions.getL()) {

            String eventType = findEventType(objectTransition);

            switch (eventType) {
                case "VectorEventCategory":
                    //TODO this restrain our implementation for only detecting changes on these properties;
                    String[] propertyNames = {"center"};
                    for(String propertyName: propertyNames) {
                        Idea eventCategory = eventCatFactory.createVectorEventCategory(propertyName, objectTransition, INIT_RELEVANCE);
                        rcvEventCats.getL().add(eventCategory);
                    }
                    break;
                case "AppearanceEventCategory":
                    Idea eventCategory = eventCatFactory.createAppearanceEventCategory(objectTransition, INIT_RELEVANCE);
                    rcvEventCats.getL().add(eventCategory);
                    break;
            }
        }
        return rcvEventCats;
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
