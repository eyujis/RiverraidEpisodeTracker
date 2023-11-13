package org.example.mind.codelets.event_tracker;

import br.unicamp.cst.representation.idea.Idea;

public class VectorEventPair {
    private Idea previousEvent;
    private Idea currentEvent;

    public VectorEventPair(Idea previousEvent, Idea currentEvent) {
        this.previousEvent = previousEvent;
        this.currentEvent = currentEvent;
    }

    public Idea getPreviousEvent() {
        return previousEvent;
    }

    public Idea getCurrentEvent() {
        return currentEvent;
    }
}
