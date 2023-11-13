package org.example.mind.codelets.event_tracker;

import br.unicamp.cst.representation.idea.Idea;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class AppearanceEventProcessor {
    Idea resultAppearanceEvents;
    ArrayList<EventPair> eventAppearancePairsToBeExtended;

    public AppearanceEventProcessor() {
        resultAppearanceEvents = new Idea("ResultAppearanceEvents", "", 0);
        eventAppearancePairsToBeExtended = new ArrayList<>();
    }

    public void process(Idea previousAppearanceEvents, Idea currentAppearanceEvents) {
        addAppearanceEventPairsToBeExtended(previousAppearanceEvents, currentAppearanceEvents);

        ArrayList<Integer> previousEventIdsInEventsToBeExtended = (ArrayList<Integer>) eventAppearancePairsToBeExtended.stream()
                .map(event -> (Integer) event.getPreviousEvent().get("eventId").getValue())
                .collect(Collectors.toList());

        ArrayList<Integer> currentEventIdsInEventsToBeExtended = (ArrayList<Integer>) eventAppearancePairsToBeExtended.stream()
                .map(event -> (Integer) event.getCurrentEvent().get("eventId").getValue())
                .collect(Collectors.toList());

        // events that have ended
        ArrayList<Idea> previousEventsNotExtended = (ArrayList<Idea>) previousAppearanceEvents.getL().stream()
                .filter(event -> !previousEventIdsInEventsToBeExtended.contains(event.get("eventId").getValue()))
                .collect(Collectors.toList());
        previousEventsNotExtended.stream().forEach(event -> event.get("hasFinished").setValue(true));

        // new events
        ArrayList<Idea> currentEventsNotExtended = (ArrayList<Idea>) currentAppearanceEvents.getL().stream()
                .filter(event -> !currentEventIdsInEventsToBeExtended.contains(event.get("eventId").getValue()))
                .collect(Collectors.toList());

        resultAppearanceEvents.getL().addAll(previousEventsNotExtended);
        resultAppearanceEvents.getL().addAll(currentEventsNotExtended);

        ArrayList<Idea> extendedEvents = extendAppearanceEvents();
        resultAppearanceEvents.getL().addAll(extendedEvents);
    }

    private ArrayList<Idea> extendAppearanceEvents() {
        ArrayList<Idea> extendedEvents = new ArrayList<>();

        for (EventPair eventAppearancePairToBeExtended: eventAppearancePairsToBeExtended) {
            Idea extendedEvent = extendAppearanceEvent(eventAppearancePairToBeExtended);
            extendedEvents.add(extendedEvent);
        }

        return extendedEvents;
    }

    private Idea extendAppearanceEvent(EventPair eventAppearancePairToBeExtended) {
        Idea previousAppearanceEvent = eventAppearancePairToBeExtended.getPreviousEvent();
        Idea currentAppearanceEvent = eventAppearancePairToBeExtended.getCurrentEvent();

        Idea extendedAppearanceEvent = previousAppearanceEvent.clone();

        //update timestamp
        int currentTimestamp = (int) currentAppearanceEvent.get("currentTimestamp").getValue();
        extendedAppearanceEvent.get("currentTimestamp").setValue(currentTimestamp);

        return extendedAppearanceEvent;
    }


    private void addAppearanceEventPairsToBeExtended(Idea previousAppearanceEvents, Idea currentAppearanceEvents) {
        for(Idea currentVEvent: currentAppearanceEvents.getL()) {
            int i = getMatchingEventIdx(currentVEvent, previousAppearanceEvents);
            if(i!=-1) {
                addAppearanceEventPairToBeExtended(currentVEvent, previousAppearanceEvents.getL().get(i));
            }
        }
    }

    private int getMatchingEventIdx(Idea currentAEvent, Idea previousAppearanceEvents) {
        int cObjectId = (int) currentAEvent.get("objectId").getValue();
        String cAEventType = (String) currentAEvent.get("appearanceEventType").getValue();

        for(int i=0; i<previousAppearanceEvents.getL().size(); i++) {
            Idea previousVEvent = previousAppearanceEvents.getL().get(i);

            int pObjectId = (int) previousVEvent.get("objectId").getValue();
            String pAEventType = (String) previousVEvent.get("appearanceEventType").getValue();

            if(cObjectId==pObjectId && cAEventType.equals(pAEventType)) {
                return i;
            }
        }
        return -1;
    }

    private void addAppearanceEventPairToBeExtended(Idea previousEvent, Idea currentEvent) {
        eventAppearancePairsToBeExtended.add(new EventPair(currentEvent, previousEvent));
    }

    public Idea getResultAppearanceEvents() {
        return resultAppearanceEvents;
    }
}
