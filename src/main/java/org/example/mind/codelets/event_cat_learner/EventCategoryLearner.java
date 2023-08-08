package org.example.mind.codelets.event_cat_learner;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_cat_learner.entities.EntityCategory;
import org.example.mind.codelets.object_cat_learner.entities.ObjectCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class EventCategoryLearner {
    Idea eventCategoryList;
    EventCategoryFactory eventCatFactory;

    double RELEVANCE_THRESHOLD = 5;
    double INIT_RELEVANCE = 1;
    double INCREMENT_FACTOR = 2.2;
    double DECREMENT_FACTOR = 0.5;
    double MINIMUM_RELEVANCE = 0.5;

    public EventCategoryLearner() {
        eventCatFactory = new EventCategoryFactory();
        eventCategoryList = new Idea("EventCategories", "", 0);
    }

    public void updateCategories(Idea objectsBuffer) {
        Idea objectsTransitions = extractObjectsPropertyTransitions(objectsBuffer);
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
            String[] propertyNames = {"center", "size"};
            for(String propertyName: propertyNames) {
                Idea eventCategory = eventCatFactory.createEventCategory(propertyName, objectTransition, INIT_RELEVANCE);
                rcvEventCats.add(eventCategory);
            }
        }
        return rcvEventCats;
    }

    public Idea extractObjectsPropertyTransitions(Idea objectsBuffer) {

        Idea objectsTransitions = new Idea("objectsTransitions", "", 0);
        List<Integer> interIds = objectIdsIntersectingBetweenTimesSteps(objectsBuffer);

        for (int id: interIds) {
            Idea objectTransition = new Idea("objectTransition", "", 0);
            objectTransition.add(new Idea("objectId", id));
            Idea propertyTimeSteps = new Idea("timeSteps", "", 0);

            for (int i=0; i<objectsBuffer.getL().size(); i++) {

                for (Idea object : objectsBuffer.getL().get(i).get("objects").getL()) {
                    int objId = (int) object.get("id").getValue();

                    if(id==objId) {
                        Idea timeStep = new Idea("timeStep", "", 0);
                        for(Idea propertyState : object.getL()) {
                            if(propertyState.getName()!="id" && propertyState.getName()!="colorId") {
                                timeStep.add(propertyState);
                            }
                        }
                        timeStep.add(objectsBuffer.getL().get(i).get("timestamp"));
                        propertyTimeSteps.add(timeStep);
                    }
                }
            }
            objectTransition.add(propertyTimeSteps);
            objectsTransitions.add(objectTransition);
        }

        return objectsTransitions;
    }

    // TODO this restricts the implementation only for objects present in all frames.
    // It will not cover the events where objects appear and disappear (e.g., explosions).
    public List<Integer> objectIdsIntersectingBetweenTimesSteps(Idea objectsBuffer) {
        List<Integer> resultIds = extractIdsFromObjects(objectsBuffer.getL().get(0).get("objects"));

        for (int i=1; i<objectsBuffer.getL().size(); i++) {
            List<Integer> currentIds = extractIdsFromObjects(objectsBuffer.getL().get(i).get("objects"));

            // source: https://www.baeldung.com/java-lists-intersection
            resultIds = resultIds.stream()
                    .distinct()
                    .filter(currentIds::contains)
                    .collect(Collectors.toList());
        }

        return resultIds;
    }


    public List<Integer> extractIdsFromObjects(Idea objects) {
        List<Integer> detectedObjectsIds = new ArrayList<>();

        for (int j=0; j<objects.getL().size(); j++) {
            detectedObjectsIds.add((Integer) objects.getL().get(j).get("id").getValue());
        }
        return detectedObjectsIds;
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

        return relevantCategories;
    }
}
