package org.example.mind.codelets.event_tracker;

import br.unicamp.cst.representation.idea.Idea;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
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

        Idea previouslyDetectedEvents = detectedEvents.clone();
        detectedEvents = new Idea("DetectedEvents", "", 0);

        for(Idea objectTransition: objectTransitions.getL()) {
            for(Idea eventCategoryIdea: eventCategories.getL()) {
                double membership = ((EventCategory) eventCategoryIdea.getValue()).membership(objectTransition);

                if(membership==1) {
                    int previousEventIdxMatch = idxForSameObjectIdAndProperty(previouslyDetectedEvents,
                            objectTransition, eventCategoryIdea);

                    // checks if there is not a previous event which matches its property and objectId
                    if(previousEventIdxMatch==-1) {
                        Idea newEvent = createEvent(objectTransition, eventCategoryIdea);
                        detectedEvents.add(newEvent);
                    } else {
                        Idea previousEvent = previouslyDetectedEvents.getL().get(previousEventIdxMatch);
                        String previousEventCategory = (String) previousEvent.get("eventCategory").getValue();

                        if(eventCategoryIdea.getName() == previousEventCategory) {
                            extendEventVector(previousEvent, objectTransition);
                            detectedEvents.add(previousEvent);
                        } else {
                            if((boolean )previousEvent.get("hasEnded").getValue() == false) {
                                previousEvent.get("hasEnded").setValue(true);

                                int finalTimestamp = (int) objectTransition.get("timeSteps").getL().get(0).get("timestamp").getValue();
                                Idea finalTimestampIdea  = new Idea("finalTimestamp", finalTimestamp);
                                previousEvent.add(finalTimestampIdea);
                                detectedEvents.add(previousEvent);
                            }
                            Idea newEvent = createEvent(objectTransition, eventCategoryIdea);
                            detectedEvents.add(newEvent);
                        }
                    }
                }
            }
        }
    }

    public Idea createEvent(Idea objectTransition, Idea eventCategoryIdea) {
        Idea eventIdea = new Idea("event", "", 0);

        eventIdea.add(new Idea("objectId", (int) objectTransition.get("objectId").getValue()));

        eventIdea.add(new Idea("eventCategory", (String) eventCategoryIdea.getName()));

        String propertyName = (String) ((EventCategory) eventCategoryIdea.getValue()).getPropertyName();
        eventIdea.add(new Idea("propertyName", propertyName));

        Idea propertyState0 =  objectTransition.get("timeSteps").getL().get(0).get(propertyName).clone();
        propertyState0.setName("initialPosition");
        eventIdea.add(propertyState0);

        Idea initialTimestamp = objectTransition.get("timeSteps").getL().get(0).get("timestamp").clone();
        initialTimestamp.setName("initialTimestamp");
        eventIdea.add(initialTimestamp);


        double[] eventVector = extractEventVector(objectTransition.get("timeSteps"), propertyName);
        eventIdea.add(new Idea("eventVector", eventVector));

        eventIdea.add(new Idea("hasEnded", false));

        return eventIdea;
    }

    private void extendEventVector(Idea previousEvent, Idea objectTransition) {

        String propertyName = (String) previousEvent.get("propertyName").getValue();
        Idea timeSteps = objectTransition.get("timeSteps");

        int nSteps = timeSteps.getL().size();

        Idea lastStep = timeSteps.getL().get(nSteps-1);

        Idea sPropertyState = previousEvent.get("initialPosition");
        Idea ePropertyState = lastStep.get(propertyName);

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

        previousEvent.get("eventVector").setValue(rawVector);
    }

    private double[] extractEventVector(Idea timeSteps, String propertyName) {
        int nSteps = timeSteps.getL().size();

        Idea firstStep = timeSteps.getL().get(0);
        Idea lastStep = timeSteps.getL().get(nSteps-1);

        Idea sPropertyState = firstStep.get(propertyName);
        Idea ePropertyState = lastStep.get(propertyName);

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
        String currentEventProperty = ((EventCategory) eventCategoryIdea.getValue()).getPropertyName();

        for(int i=0; i<previouslyDetectedEvents.getL().size(); i++) {
            int previousEventObjId = (int) previouslyDetectedEvents.getL().get(i).get("objectId").getValue();
            String previousEventProperty = (String) previouslyDetectedEvents.getL().get(i).get("propertyName").getValue();
            if(currentEventObjId == previousEventObjId && currentEventProperty == previousEventProperty) {
                return i;
            }
        }
        return -1;
    }

    public Idea getDetectedEvents() {
        return detectedEvents;
    }
}
