package org.example.mind.codelets.event_tracker;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.event_cat_learner.*;
import org.example.mind.codelets.event_cat_learner.entities.ObjectsTransitionsExtractor;


import java.util.ArrayList;
import java.util.stream.Collectors;

public class EventTracker {
    Idea detectedEvents;
    Idea assimilatedCategories;

    ObjectsTransitionsExtractor objectsTransitionsExtractor;
    EventCategoryFactory eventCatFactory;
    double INIT_RELEVANCE = 5;

    int factoryId = 0;

    public EventTracker() {
        detectedEvents = new Idea("DetectedEvents", "", 0);
        assimilatedCategories = new Idea("AssimilatedCategories", "", 0);
        objectsTransitionsExtractor = new ObjectsTransitionsExtractor();
        eventCatFactory = new EventCategoryFactory();
    }

    // pDetectedEvents are the previously detected events.
    public void detectEvents(Idea objectsBuffer, Idea eventCategories, Idea previousEvents) {

        detectedEvents = new Idea("DetectedEvents", "", 0);
        assimilatedCategories = new Idea("AssimilatedCategories", "", 0);

        Idea objectsTransitions = objectsTransitionsExtractor.extract(objectsBuffer);

        Idea currentEvents = extractEventsFromObjectsTransitions(objectsTransitions, eventCategories);

        Idea previousVectorEvents = filterVectorEvents(previousEvents);
        Idea currentVectorEvents = filterVectorEvents(currentEvents);

        VectorEventsProcessor vectorEventsProcessor = new VectorEventsProcessor();
        vectorEventsProcessor.process(previousVectorEvents, currentVectorEvents);
        detectedEvents.getL().addAll(vectorEventsProcessor.getResultVectorEvents().getL());

        Idea previousAppearEvents = filterAppearanceEvents(previousEvents);
        Idea currentAppearEvents = filterAppearanceEvents(currentEvents);

        AppearanceEventProcessor appearanceEventProcessor = new AppearanceEventProcessor();
        appearanceEventProcessor.process(previousAppearEvents, currentAppearEvents);
        detectedEvents.getL().addAll(appearanceEventProcessor.getResultAppearanceEvents().getL());

        //update current timestamp
        detectedEvents.setValue(getCurrentTimestamp(objectsBuffer));
    }

    private Idea extractEventsFromObjectsTransitions(Idea objectsTransitions, Idea eventCategories) {
        Idea eventsFromObjsTransitions = new Idea("EventsFromObjectsTransitions", "", 0);
        for(Idea objectTransition: objectsTransitions.getL()) {
            boolean categoryDetected = false;

            for (Idea eventCategoryIdea : eventCategories.getL()) {
                double membership = ((EventCategory) eventCategoryIdea.getValue()).membership(objectTransition);
                if(membership==1) {
                    categoryDetected = true;

                    if(eventCategoryIdea.getValue() instanceof AppearanceEventCategory) {
                        Idea newEvent = createAppearanceEvent(objectTransition, eventCategoryIdea);
                        eventsFromObjsTransitions.getL().add(newEvent);
                    }

                    if(eventCategoryIdea.getValue() instanceof VectorEventCategory
                            && ((VectorEventCategory) eventCategoryIdea.getValue()).getPropertyName()=="center") {
                        Idea newEvent = createVectorEvent(objectTransition, eventCategoryIdea);
                        eventsFromObjsTransitions.getL().add(newEvent);
                    }
                }
            }

            if(!categoryDetected){
                //TODO create a new event and add assimilatedCategories
                String eventType = new EventCategoryLearner().findEventType(objectTransition);

                switch (eventType) {
                    case "VectorEventCategory":
                        //TODO this restrain our implementation for only detecting changes on these properties;
                        String[] propertyNames = {"center"};
                        for (String propertyName : propertyNames) {
                            Idea eventCategory = eventCatFactory.createVectorEventCategory(propertyName, objectTransition, INIT_RELEVANCE);
                            assimilatedCategories.getL().add(eventCategory);

                            Idea newEvent = createVectorEvent(objectTransition, eventCategory);
                            eventsFromObjsTransitions.getL().add(newEvent);

                        }
                        break;
                    case "AppearanceEventCategory":
                        Idea eventCategory = eventCatFactory.createAppearanceEventCategory(objectTransition, INIT_RELEVANCE);
                        assimilatedCategories.getL().add(eventCategory);

                        Idea newEvent = createAppearanceEvent(objectTransition, eventCategory);
                        eventsFromObjsTransitions.getL().add(newEvent);

                        break;
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

        Idea initialTimestamp = objectTransition.get("timeSteps").getL().get(0).get("timestamp").clone();
        initialTimestamp.setName("initialTimestamp");
        eventIdea.add(initialTimestamp);

        int nSteps = objectTransition.get("timeSteps").getL().size();
        Idea currentTimestamp = objectTransition.get("timeSteps").getL().get(nSteps-1).get("timestamp").clone();
        currentTimestamp.setName("currentTimestamp");
        eventIdea.add(currentTimestamp);

        int disappear = ((AppearanceEventCategory) eventCategoryIdea.getValue()).disappearance;
        if(disappear==1) {
            Idea propertyState0 =  objectTransition.get("timeSteps").getL().get(0).get("idObject").clone();
            propertyState0.setName("lastObjectState");
            eventIdea.add(propertyState0);

            eventIdea.add(new Idea("appearanceEventType", "disappear"));
        }

        if(disappear==0) {
            Idea propertyState0 =  objectTransition.get("timeSteps").getL().get(nSteps-1).get("idObject").clone();
            propertyState0.setName("lastObjectState");
            eventIdea.add(propertyState0);

            eventIdea.add(new Idea("appearanceEventType", "appear"));
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
        propertyState0.setName("initialPropertyState");
        eventIdea.add(propertyState0);

        Idea initialTimestamp = objectTransition.get("timeSteps").getL().get(0).get("timestamp").clone();
        initialTimestamp.setName("initialTimestamp");
        eventIdea.add(initialTimestamp);

        int nSteps = objectTransition.get("timeSteps").getL().size();
        Idea currentTimestamp = objectTransition.get("timeSteps").getL().get(nSteps-1).get("timestamp").clone();
        currentTimestamp.setName("currentTimestamp");
        eventIdea.add(currentTimestamp);

        Idea currentObjectState = objectTransition.get("timeSteps").getL().get(nSteps-1).get("idObject").clone();
        currentObjectState.setName("lastObjectState");
        eventIdea.add(currentObjectState);

        double[] eventVector = extractEventVector(objectTransition.get("timeSteps"), propertyName);
        eventIdea.add(new Idea("eventVector", eventVector));

        return eventIdea;
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

    private Idea filterVectorEvents(Idea events) {
        Idea vectorEvents = new Idea("VectorEvents", "", 0);

        ArrayList<Idea> vectorEventsChildren = (ArrayList<Idea>) events.getL().stream()
                .filter(event -> ((String) event.get("eventCategory").getValue()).startsWith("VectorEvent"))
                .collect(Collectors.toList());

        vectorEvents.getL().addAll(vectorEventsChildren);
        return vectorEvents;
    }

    private Idea filterAppearanceEvents(Idea events) {
        Idea appearEvents = new Idea("AppearanceEvents", "", 0);

        ArrayList<Idea> appearEventsChildren = (ArrayList<Idea>) events.getL().stream()
                .filter(event -> ((String) event.get("eventCategory").getValue()).startsWith("AppearanceEvent"))
                .collect(Collectors.toList());

        appearEvents.getL().addAll(appearEventsChildren);
        return appearEvents;
    }

    private int getCurrentTimestamp(Idea objectsBuffer) {
        int bufferSize = objectsBuffer.getL().size()-1;
        int timestamp = (int) objectsBuffer.getL().get(bufferSize).get("timestamp").getValue();
        return timestamp;
    }

    public Idea getAssimilatedCategories() {
        return assimilatedCategories;
    }

    public Idea getDetectedEvents() {
        return detectedEvents;
    }

    public int generateEventId() {
        factoryId++;
        return factoryId;
    }
}