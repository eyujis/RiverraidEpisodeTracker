package org.example.mind.codelets.event_cat_learner;

import br.unicamp.cst.representation.idea.Category;
import br.unicamp.cst.representation.idea.Idea;

import java.util.List;

public class EventCategory implements Category {
    @Override
    public double membership(Idea idea) {
        return 0;
    }

    @Override
    public Idea getInstance(List<Idea> constraints) {
        return null;
    }
}
