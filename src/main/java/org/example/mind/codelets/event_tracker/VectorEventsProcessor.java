package org.example.mind.codelets.event_tracker;

import br.unicamp.cst.representation.idea.Idea;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class VectorEventsProcessor {
    double MIN_ANGLE_DIFF = 0.5 * Math.PI; // 45 degree angle

    Idea resultVectorEvents;
    ArrayList<EventPair> eventVectorPairsToBeExtended;

    public VectorEventsProcessor() {
        resultVectorEvents = new Idea("ResultVectorEvents", "", 0);
        eventVectorPairsToBeExtended = new ArrayList<>();
    }

    public void process(Idea previousVectorEvents, Idea currentVectorEvents) {
        addVectorEventPairsToBeExtended(previousVectorEvents, currentVectorEvents);

        ArrayList<Integer> previousEventIdsInEventsToBeExtended = (ArrayList<Integer>) eventVectorPairsToBeExtended.stream()
                .map(event -> (Integer) event.getPreviousEvent().get("eventId").getValue())
                .collect(Collectors.toList());

        ArrayList<Integer> currentEventIdsInEventsToBeExtended = (ArrayList<Integer>) eventVectorPairsToBeExtended.stream()
                .map(event -> (Integer) event.getCurrentEvent().get("eventId").getValue())
                .collect(Collectors.toList());

        // events that have ended
        ArrayList<Idea> previousEventsNotExtended = (ArrayList<Idea>) previousVectorEvents.getL().stream()
                .filter(event -> !previousEventIdsInEventsToBeExtended.contains(event.get("eventId").getValue()))
                .collect(Collectors.toList());
        previousEventsNotExtended.stream().forEach(event -> event.get("hasFinished").setValue(true));

        // new events
        ArrayList<Idea> currentEventsNotExtended = (ArrayList<Idea>) currentVectorEvents.getL().stream()
                .filter(event -> !currentEventIdsInEventsToBeExtended.contains(event.get("eventId").getValue()))
                .collect(Collectors.toList());
        resultVectorEvents.getL().addAll(previousEventsNotExtended);
        resultVectorEvents.getL().addAll(currentEventsNotExtended);

        ArrayList<Idea> extendedEvents = extendVectorEvents();
        resultVectorEvents.getL().addAll(extendedEvents);
    }

    private ArrayList<Idea> extendVectorEvents() {
        ArrayList<Idea> extendedEvents = new ArrayList<>();

        for (EventPair eventVectorPairToBeExtended: eventVectorPairsToBeExtended) {
            Idea extendedEvent = extendVectorEvent(eventVectorPairToBeExtended);
            extendedEvents.add(extendedEvent);
        }

        return extendedEvents;
    }

    private Idea extendVectorEvent(EventPair eventVectorPairToBeExtended) {
        Idea previousVectorEvent = eventVectorPairToBeExtended.getPreviousEvent();
        Idea currentVectorEvent = eventVectorPairToBeExtended.getCurrentEvent();

        Idea extendedVectorEvent = previousVectorEvent.clone();

        //update timestamp
        int currentTimestamp = (int) currentVectorEvent.get("currentTimestamp").getValue();
        extendedVectorEvent.get("currentTimestamp").setValue(currentTimestamp);

        Idea previousEventInitialState = previousVectorEvent.get("initialPropertyState");

        Idea currentEventInitialState = currentVectorEvent.get("initialPropertyState");
        double[] currentEventEventVector = (double[]) currentVectorEvent.get("eventVector").getValue();
        double[] currentEventFinalState = new double[currentEventEventVector.length];
        // produce the current event final state
        for(int i=0; i<currentEventFinalState.length; i++) {
            currentEventFinalState[i] = (double) currentEventInitialState.getL().get(i).getValue()
                    + currentEventEventVector[i];
        }

        double[] resultVector = new double[currentEventFinalState.length];

        for(int i=0; i<resultVector.length; i++) {
            resultVector[i] = currentEventFinalState[i] - (double) previousEventInitialState.getL().get(i).getValue();
        }

        extendedVectorEvent.get("eventVector").setValue(resultVector);

        return extendedVectorEvent;
    }

    private void addVectorEventPairsToBeExtended(Idea previousVectorEvents, Idea currentVectorEvents) {
        for(Idea currentVEvent: currentVectorEvents.getL()) {
            int i = getMatchingEventIdx(currentVEvent, previousVectorEvents);
            if(i!=-1) {
                addVectorEventPairToBeExtended(previousVectorEvents.getL().get(i), currentVEvent);
            }
        }
    }

    private int getMatchingEventIdx(Idea currentVEvent, Idea previousVectorEvents) {
        int j=-1;

        int cObjectId = (int) currentVEvent.get("objectId").getValue();
        String cEventProperty = (String) currentVEvent.get("propertyName").getValue();
        String cEventCategory = (String) currentVEvent.get("eventCategory").getValue();
        double[] cEventVector = (double[]) currentVEvent.get("eventVector").getValue();
        boolean cEventHasFinished = (boolean) currentVEvent.get("hasFinished").getValue();

        for(int i=0; i<previousVectorEvents.getL().size(); i++) {
            Idea previousVEvent = previousVectorEvents.getL().get(i);

            int pObjectId = (int) previousVEvent.get("objectId").getValue();
            String pEventProperty = (String) previousVEvent.get("propertyName").getValue();
            String pEventCategory = (String) previousVEvent.get("eventCategory").getValue();
            double[] pEventVector = (double[]) previousVEvent.get("eventVector").getValue();
            boolean pEventHasFinished = (boolean) previousVEvent.get("hasFinished").getValue();

            if(cObjectId == pObjectId && cEventProperty.equals(pEventProperty) && cEventCategory.equals(pEventCategory)
                    && hasSimilarAngle(cEventVector, pEventVector)
                    && !cEventHasFinished
                    && !pEventHasFinished
                    ) {
                j=i;
            }
        }
        return j;
    }

    private void addVectorEventPairToBeExtended(Idea previousEvent, Idea currentEvent) {
        eventVectorPairsToBeExtended.add(new EventPair(previousEvent, currentEvent));
    }

    public boolean hasSimilarAngle(double[] rawVector1, double[] rawVector2) {

        RealVector eventVector1 = new ArrayRealVector(rawVector1);
        RealVector eventVector2 = new ArrayRealVector(rawVector2);

        if(isZeroMagnitude(eventVector1) && isZeroMagnitude(eventVector2)) {
            return true;
        }

        double angleDiff = getAngleDiff(eventVector1, eventVector2);

        if(angleDiff<=MIN_ANGLE_DIFF) {
            return true;
        }
        return false;
    }

    private boolean isZeroMagnitude(RealVector vector) {
        if(vector.getNorm()==0) {
            return true;
        }
        return false;
    }

    private double getAngleDiff(RealVector vectorA, RealVector vectorB) {
        double dotProduct = vectorA.dotProduct(vectorB);

        double aMag = vectorA.getNorm();
        double bMag = vectorB.getNorm();

        double cosAngle = dotProduct/(aMag*bMag);
        double angleDiff = Math.acos(cosAngle);

        return Math.abs(angleDiff);
    }

    public Idea getResultVectorEvents() {
        return resultVectorEvents;
    }
}
