package org.example.mind.codelets.object_cat_learner;

import org.example.mind.codelets.object_cat_learner.entities.PObjectCategory;
import org.example.mind.codelets.object_proposer_codelet.entities.RRObject;

import java.util.ArrayList;
import java.util.Iterator;

public class PObjectCategoryLearner {
    private ArrayList<PObjectCategory> objCategoryList;

    double RELEVANCE_THRESHOLD = 5;
    double INIT_RELEVANCE = 1;
    double INCREMENT_FACTOR = 2.2;
    double DECREMENT_FACTOR = 0.5;
    double MINIMUM_RELEVANCE = 0.5;

    public PObjectCategoryLearner() {

        objCategoryList = new ArrayList<PObjectCategory>();
    }

    public void updateCategories(ArrayList<RRObject> objectInstances) {

        // find categories and increment relevance of existing categories
        for(RRObject objectInstance : objectInstances) {
            int assignedCatIdx = this.belongToCatIdx(objectInstance);

            if(assignedCatIdx == -1) {
                PObjectCategory newObjCategory = new PObjectCategory(objectInstance.getExternalContour(),
                                                                   objectInstance.getBoundRect(),
                                                                   objectInstance.getColor(), INIT_RELEVANCE);
                this.objCategoryList.add(newObjCategory);
            } else {
                if(this.objCategoryList.get(assignedCatIdx).getRelevance()<RELEVANCE_THRESHOLD) {
                    this.objCategoryList.get(assignedCatIdx).incrementRelevance(INCREMENT_FACTOR);
                }
            }
        }

        decrementCategoriesRelevance();
        removeIrrelevantCategories();
    }

    public void removeIrrelevantCategories() {
        for(Iterator<PObjectCategory> iter = objCategoryList.iterator(); iter.hasNext(); ) {
            PObjectCategory objectCategory = iter.next();
            if(objectCategory.getRelevance() < MINIMUM_RELEVANCE) {
                iter.remove();
            }
        }
    }

    public void decrementCategoriesRelevance() {
        for(PObjectCategory objectCategory : objCategoryList) {
            if(objectCategory.getRelevance() < RELEVANCE_THRESHOLD) {
                objectCategory.decrementRelevance(DECREMENT_FACTOR);
            }
        }
    }

    public int belongToCatIdx(RRObject obj) {
        int idx = -1;

        for(int i=0; i<objCategoryList.size(); i++) {
            if(objCategoryList.get(i).membership(obj) == 1) {
                idx = i;
            }
        }
        return idx;
    }

    public ArrayList<PObjectCategory> getRelevantCategories() {
        ArrayList<PObjectCategory> relevantCategories = new ArrayList<>();

        for(PObjectCategory objectCategory : objCategoryList) {
            if(objectCategory.getRelevance()>=RELEVANCE_THRESHOLD) {
                relevantCategories.add(objectCategory);
            }
        }

        return relevantCategories;
    }
}
