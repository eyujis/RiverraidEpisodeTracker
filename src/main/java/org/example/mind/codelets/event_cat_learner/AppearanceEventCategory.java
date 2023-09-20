package org.example.mind.codelets.event_cat_learner;
import br.unicamp.cst.representation.idea.Idea;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppearanceEventCategory implements EventCategory {
    String objectCategory;
    double relevance;
    public int disappearance;

    public AppearanceEventCategory(Idea objectTransition, double relevance) {
        this.relevance = relevance;
        this.objectCategory = getObjectCategoryName(objectTransition);
        this.disappearance = doesObjectDisappears(objectTransition);
    }
    @Override
    public Idea getInstance(List<Idea> list) {
        return null;
    }

    @Override
    public double membership(Idea objectTransition) {
        if(this.objectCategory != getObjectCategoryName(objectTransition)) {
            return 0;
        }
        if(this.disappearance != doesObjectDisappears(objectTransition)
        || doesObjectDisappears(objectTransition) == -1) {
            return 0;
        }

        return 1;
    }


    @Override
    public boolean sameCategory(EventCategory compCat) {
        if(!(compCat instanceof AppearanceEventCategory)) {
            return false;
        }
        AppearanceEventCategory appearsCompCat = (AppearanceEventCategory) compCat;

        if (this.objectCategory == appearsCompCat.objectCategory
        && this.disappearance == appearsCompCat.disappearance) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public double getRelevance() {
        return relevance;
    }

    @Override
    public void decrementRelevance(double discount) {
        this.relevance *= discount;
    }
    @Override
    public void incrementRelevance(double increment) {
        this.relevance *= increment;
    }

    private String getObjectCategoryName(Idea objectTransition) {
        String categoryName = null;
        for(Idea timeStep: objectTransition.get("timeSteps").getL()) {
            Idea object = timeStep.get("idObject");

            if(object!=null) {
                String currCategoryName = (String) object.get("objectCategory").getValue();

                if(categoryName==null || categoryName==currCategoryName) {
                    categoryName = currCategoryName;
                } else {
                    Logger.getLogger(AppearanceEventCategory.class.getName()).log(Level.SEVERE,
                            "object category changes between time steps.");
                }
            }

        }
        return categoryName;
    }


    // object disappears = 1
    // object appears = 0
    // neither = -1
    private int doesObjectDisappears(Idea objectTransition) {
        List<Idea> timeSteps = objectTransition.get("timeSteps").getL();
        int nSteps = timeSteps.size();

        Idea firstStepObject = timeSteps.get(0).get("idObject");
        Idea lastStepObject = timeSteps.get(nSteps-1).get("idObject");

        if(firstStepObject==null && lastStepObject!=null) {
            return 0;
        }

        if(firstStepObject!=null && lastStepObject==null) {
            return 1;
        }

        return -1;
    }
}
