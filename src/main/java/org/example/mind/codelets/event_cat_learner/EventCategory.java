package org.example.mind.codelets.event_cat_learner;

import br.unicamp.cst.representation.idea.Category;

public interface EventCategory extends Category {
    public void incrementRelevance(double increment);
    public void decrementRelevance(double discount);
    public boolean sameCategory(EventCategory compCat);
    public double getRelevance();
}
