package org.example.mind.codelets.event_tracker;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.event_cat_learner.AppearanceEventCategory;
import org.example.mind.codelets.event_cat_learner.VectorEventCategory;
import org.example.mind.codelets.event_cat_learner.EventCategory;
import org.example.mind.codelets.event_cat_learner.entities.ObjectsTransitionsExtractor;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class EventTracker {
    Idea detectedEvents;
    ObjectsTransitionsExtractor objectsTransitionsExtractor;
    int factoryId = 0;

    public EventTracker() {
        detectedEvents = new Idea("DetectedEvents", "", 0);
        objectsTransitionsExtractor = new ObjectsTransitionsExtractor();
    }

    // pDetectedEvents are the previously detected events.
    public void detectEvents(Idea objectsBuffer, Idea eventCategories, Idea previousEvents) {
        detectedEvents = new Idea("DetectedEvents", "", 0);
        Idea objectsTransitions = objectsTransitionsExtractor.extract(objectsBuffer);

        Idea currentEvents = extractEventsFromObjectsTransitions(objectsTransitions, eventCategories);

        Idea previousVectorEvents = filterVectorEvents(previousEvents);
        Idea currentVectorEvents = filterVectorEvents(currentEvents);




        for(Idea objectTransition: objectsTransitions.getL()) {
            for(Idea eventCategoryIdea: eventCategories.getL()) {
                double membership = ((EventCategory) eventCategoryIdea.getValue()).membership(objectTransition);

                if(membership==1 && eventCategoryIdea.getValue() instanceof VectorEventCategory) {
                    int previousEventIdxMatch = idxForSameObjectIdAndProperty(previousEvents,
                            objectTransition, eventCategoryIdea);

                    // checks if there is not a previous event which matches its property and objectId
                    if(previousEventIdxMatch==-1) {
                        Idea newEvent = createVectorEvent(objectTransition, eventCategoryIdea);
                        detectedEvents.add(newEvent);
                    } else {
                        Idea previousEvent = previousEvents.getL().get(previousEventIdxMatch);
                        String previousEventCategory = (String) previousEvent.get("eventCategory").getValue();

                        // checks if the total event vector has a similar angle compared with the event category
                        double[] previousEventVector = (double[]) previousEvent.get("eventVector").getValue();
                        boolean hasSimilarAngle = ((VectorEventCategory) eventCategoryIdea.getValue()).hasSimilarAngle(previousEventVector);

                        if(eventCategoryIdea.getName() == previousEventCategory && hasSimilarAngle) {
                            extendEventVector(previousEvent, objectTransition);
                            updateCurrentTimestamp(previousEvent, objectTransition);
                            detectedEvents.add(previousEvent);
                        } else {
                            Idea newEvent = createVectorEvent(objectTransition, eventCategoryIdea);
                            detectedEvents.add(newEvent);
                        }
                    }
                }

                if(membership==1 && eventCategoryIdea.getValue() instanceof AppearanceEventCategory) {
                    Idea newEvent = createAppearanceEvent(objectTransition, eventCategoryIdea);
                    detectedEvents.add(newEvent);
                }
            }
        }

        updateHasFinished(previousEvents, detectedEvents);
    }

    private Idea extractEventsFromObjectsTransitions(Idea objectsTransitions, Idea eventCategories) {
        Idea eventsFromObjsTransitions = new Idea("EventsFromObjectsTransitions", "", 0);

        for(Idea objectTransition: objectsTransitions.getL()) {
            for (Idea eventCategoryIdea : eventCategories.getL()) {
                double membership = ((EventCategory) eventCategoryIdea.getValue()).membership(objectTransition);
                if(membership==1) {
                    Idea newEvent = null;

                    if(eventCategoryIdea.getValue() instanceof VectorEventCategory) {
                        newEvent = createVectorEvent(objectTransition, eventCategoryIdea);
                    }
                    if(eventCategoryIdea.getValue() instanceof AppearanceEventCategory) {
                        newEvent = createAppearanceEvent(objectTransition, eventCategoryIdea);
                    }
                    eventsFromObjsTransitions.add(newEvent);
                }
            }
        }
        return eventsFromObjsTransitions;
    }

    public Idea createAppearanceEvent(Idea objectTransition, Idea eventCategoryIdea) {
        Idea eventIdea = new Idea("event", "", 0);

        eventIdea.add(new Idea("eventId", generateEventId()));

        eventIdea.add(new Idea("hasFinished", false));

        eventIdea.add(new Idea("objectId", (int) objectTransition.get("objectId").getValue()));

        eventIdea.add(new Idea("eventCategory", (String) eventCategoryIdea.getName()));

        int disappear = ((AppearanceEventCategory) eventCategoryIdea.getValue()).disappearance;
        if(disappear==1) {
            Idea propertyState0 =  objectTransition.get("timeSteps").getL().get(0).get("idObject").get("center").clone();
            propertyState0.setName("disappearPosition");
            eventIdea.add(propertyState0);
        }

        if(disappear==0) {
            int nSteps = objectTransition.get("timeSteps").getL().size();
            Idea propertyState0 =  objectTransition.get("timeSteps").getL().get(nSteps-1).get("idObject").get("center").clone();
            propertyState0.setName("appearPosition");
            eventIdea.add(propertyState0);
        }

        return eventIdea;
    }

    public Idea createVectorEvent(Idea objectTransition, Idea eventCategoryIdea) {
        Idea eventIdea = new Idea("event", "", 0);

        eventIdea.add(new Idea("eventId", generateEventId()));

        eventIdea.add(new Idea("hasFinished", false));

        eventIdea.add(new Idea("objectId", (int) objectTransition.get("objectId").getValue()));

        eventIdea.add(new Idea("eventCategory", (String) eventCategoryIdea.getName()));

        String propertyName = (String) ((VectorEventCategory) eventCategoryIdea.getValue()).getPropertyName();
        eventIdea.add(new Idea("propertyName", propertyName));

        Idea propertyState0 =  objectTransition.get("timeSteps").getL().get(0).get("idObject").get(propertyName).clone();
        propertyState0.setName("initialPosition");
        eventIdea.add(propertyState0);

        Idea initialTimestamp = objectTransition.get("timeSteps").getL().get(0).get("timestamp").clone();
        initialTimestamp.setName("initialTimestamp");
        eventIdea.add(initialTimestamp);

        int nSteps = objectTransition.get("timeSteps").getL().size();
        Idea currentTimestamp = objectTransition.get("timeSteps").getL().get(nSteps-1).get("timestamp").clone();
        currentTimestamp.setName("currentTimestamp");
        eventIdea.add(currentTimestamp);


        double[] eventVector = extractEventVector(objectTransition.get("timeSteps"), propertyName);
        eventIdea.add(new Idea("eventVector", eventVector));

        return eventIdea;
    }

    private void extendEventVector(Idea previousEvent, Idea objectTransition) {

        String propertyName = (String) previousEvent.get("propertyName").getValue();
        Idea timeSteps = objectTransition.get("timeSteps");

        int nSteps = timeSteps.getL().size();

        Idea lastObjectStep = timeSteps.getL().get(nSteps-1).get("idObject");

        Idea initialPropertyState = previousEvent.get("initialPosition");
        Idea finalPropertyState = lastObjectStep.get(propertyName);

        double[] rawVector = new double[initialPropertyState.getL().size()];

        if(initialPropertyState.getL().size()==finalPropertyState.getL().size()) {
            for(int i=0; i<initialPropertyState.getL().size(); i++) {
                double startValue = (double) initialPropertyState.getL().get(i).getValue();
                double endValue = (double) finalPropertyState.getL().get(i).getValue();
                rawVector[i] = endValue-startValue;
            }
        } else {
            System.out.println("Property with inconsistent number of quality dimensions");
        }

        previousEvent.get("eventVector").setValue(rawVector);
    }

    private void updateCurrentTimestamp(Idea previousEvent, Idea objectTransition) {
        int nSteps = objectTransition.get("timeSteps").getL().size();
        int currentTimestamp = (int) objectTransition.get("timeSteps").getL().get(nSteps-1).get("timestamp").clone().getValue();
        previousEvent.get("currentTimestamp").setValue(currentTimestamp);
    }

    private double[] extractEventVector(Idea timeSteps, String propertyName) {
        int nSteps = timeSteps.getL().size();

        Idea firstObjectStep = timeSteps.getL().get(0).get("idObject");
        Idea lastObjectStep = timeSteps.getL().get(nSteps-1).get("idObject");

        Idea sPropertyState = firstObjectStep.get(propertyName);
        Idea ePropertyState = lastObjectStep.get(propertyName);

        double[] rawVector = new double[sPropertyState.getL().size()];

        if(sPropertyState.getL().size()==ePropertyState.getL().size()) {
            for(int i=0; i<sPropertyState.getL().size(); i++) {
                double startValue = (double) sPropertyState.getL().get(i).getValue();
                double endValue = (double) ePropertyState.getL().get(i).getValue();
                rawVector[i] = endValue-startValue;
            }
        } else {
            System.out.println("Property with inconsistent number of quality dimensions");
        }

        return rawVector;
    }

    public int idxForSameObjectIdAndProperty(Idea previouslyDetectedEvents, Idea objectTransition, Idea eventCategoryIdea) {

        int currentEventObjId = (int) objectTransition.get("objectId").getValue();
        String currentEventProperty = ((VectorEventCategory) eventCategoryIdea.getValue()).getPropertyName();

        for(int i=0; i<previouslyDetectedEvents.getL().size(); i++) {
            String previousEventCategoryName = (String) previouslyDetectedEvents.getL().get(i).get("eventCategory").getValue();
            if(previousEventCategoryName.startsWith("VectorEventCategory")) {
                int previousEventObjId = (int) previouslyDetectedEvents.getL().get(i).get("objectId").getValue();
                String previousEventProperty = (String) previouslyDetectedEvents.getL().get(i).get("propertyName").getValue();
                if(currentEventObjId == previousEventObjId && currentEventProperty == previousEventProperty) {
                    return i;
                }
            }
        }
        return -1;
    }

    private Idea filterVectorEvents(Idea events) {
        Idea vectorEvents = new Idea("VectorEvents", "", 0);

        ArrayList<Idea> vectorEventsChildren = (ArrayList<Idea>) events.getL().stream()
                .filter(event -> ((String) event.get("eventCategory").getValue()).startsWith("VectorEvent"))
                .collect(Collectors.toList());

        vectorEvents.getL().addAll(vectorEventsChildren);
        return vectorEvents;
    }

    private void updateHasFinished(Idea previouslyDetectedEvents, Idea detectedEvents) {
        // get all event ids from the current detected events
        ArrayList<Integer> detectedEventsIds = (ArrayList<Integer>) detectedEvents.getL().stream()
                .map(event -> (Integer) event.get("eventId").getValue()).collect(Collectors.toList());

        // finished events are the one present in the last event list and not present in the current
        ArrayList<Idea> finishedEvents = (ArrayList<Idea>) previouslyDetectedEvents.getL().stream()
                .filter(event -> !detectedEventsIds.contains((Integer) event.get("eventId").getValue()))
                .collect(Collectors.toList());

        finishedEvents.stream().forEach(event -> event.get("hasFinished").setValue(true));
    }

    public Idea getDetectedEvents() {
        return detectedEvents;
    }

    public int generateEventId() {
        factoryId++;
        return factoryId;
    }

}
