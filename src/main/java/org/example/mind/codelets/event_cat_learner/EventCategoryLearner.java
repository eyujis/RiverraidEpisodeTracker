package org.example.mind.codelets.event_cat_learner;

import br.unicamp.cst.representation.idea.Idea;
import jdk.jfr.Event;
import org.example.mind.codelets.event_cat_learner.entities.ObjectsTransitionsExtractor;
import org.example.mind.codelets.object_cat_learner.entities.EntityCategory;
import org.example.mind.codelets.object_cat_learner.entities.ObjectCategory;

import java.nio.file.FileSystemNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class EventCategoryLearner {
    Idea eventCategoryList;
    EventCategoryFactory eventCatFactory;
    ObjectsTransitionsExtractor objectsTransitionsExtractor;

    double RELEVANCE_THRESHOLD = 5;
    double INIT_RELEVANCE = 1;
    double INCREMENT_FACTOR = 2.2;
    double DECREMENT_FACTOR = 0.5;
    double MINIMUM_RELEVANCE = 0.5;

    public EventCategoryLearner() {
        eventCatFactory = new EventCategoryFactory();
        eventCategoryList = new Idea("EventCategories", "", 0);
        objectsTransitionsExtractor = new ObjectsTransitionsExtractor();
    }

    public void updateCategories(Idea objectsBuffer) {
        Idea objectsTransitions = objectsTransitionsExtractor.extract(objectsBuffer);
        Idea rcvEventCats = extractEventCategories(objectsTransitions);

        for(Idea rcvCat : rcvEventCats.getL()) {
            int equalCatIdx = this.equalCategoryIdx(rcvCat);

            if(equalCatIdx == -1) {
                eventCategoryList.add(rcvCat);
            } else {
                EventCategory eventCat = (EventCategory) eventCategoryList.getL().get(equalCatIdx).getValue();
                if(eventCat.getRelevance()<RELEVANCE_THRESHOLD) {
                    eventCat.incrementRelevance(INCREMENT_FACTOR);
                }
            }
        }
        decrementCategoriesRelevance();
        removeIrrelevantCategories();
    }

    public Idea extractEventCategories(Idea objectsTransitions) {
        Idea rcvEventCats = new Idea("extractedEventCategories", "", 0);
        for(Idea objectTransition: objectsTransitions.getL()) {

            //TODO this restrain our implementation for only detecting changes on these properties;
            // I should loop instead through all object's properties, I don't do that because
            // the bounding box is a property which contains properties;
//            String[] propertyNames = {"center", "size"};
            String[] propertyNames = {"center"};
            for(String propertyName: propertyNames) {
                Idea eventCategory = eventCatFactory.createEventCategory(propertyName, objectTransition, INIT_RELEVANCE);
                rcvEventCats.add(eventCategory);
            }
        }
        return rcvEventCats;
    }

    public int equalCategoryIdx(Idea cat) {
        int idx = -1;

        for(int i=0; i<eventCategoryList.getL().size(); i++) {
            EventCategory eventCatListElem = (EventCategory) eventCategoryList.getL().get(i).getValue();
            EventCategory eventInstance = (EventCategory) cat.getValue();

            if(eventCatListElem.equals(eventInstance) == true) {
                idx = i;
            }
        }

        return idx;
    }

    public void decrementCategoriesRelevance() {
        for(Idea eventCategoryIdea: eventCategoryList.getL()) {
            EventCategory eventCategory = (EventCategory) eventCategoryIdea.getValue();
            if(eventCategory.getRelevance() < RELEVANCE_THRESHOLD) {
                eventCategory.decrementRelevance(DECREMENT_FACTOR);
            }
        }
    }

    public void removeIrrelevantCategories() {
        ArrayList<Integer> idxsToRemove = new ArrayList();

        for(int i=0; i<eventCategoryList.getL().size(); i++) {
            Idea eventCatIdea = eventCategoryList.getL().get(i);
            EventCategory eventCat = (EventCategory) eventCatIdea.getValue();
            if(eventCat.getRelevance() < MINIMUM_RELEVANCE) {
                idxsToRemove.add(i);
            }
        }

        removeIdxFromObjectCategoryList(idxsToRemove);
    }

    private void removeIdxFromObjectCategoryList(ArrayList<Integer> idxsToRemove) {
        Collections.sort(idxsToRemove, Collections.reverseOrder());

        for (int index : idxsToRemove) {
            if (index >= 0 && index < eventCategoryList.getL().size()) {
                eventCategoryList.getL().remove(index);
            }
        }
    }

    public Idea getRelevantCategories() {
        Idea relevantCategories = new Idea("RelevantCategories", "", 0);;

        for(Idea eventCatIdea : eventCategoryList.getL()) {
            EventCategory eventCategory = (EventCategory) eventCatIdea.getValue();
            if(eventCategory.getRelevance()>=RELEVANCE_THRESHOLD) {
                relevantCategories.add(eventCatIdea);
            }
        }

//        for(Idea relevantCategoryIdea : relevantCategories.getL()) {
//            EventCategory relevantCategory = (EventCategory) relevantCategoryIdea.getValue();
//            System.out.println(relevantCategory.getEventVector());
//        }
//        System.out.println("----------------");

        return relevantCategories;
    }
}