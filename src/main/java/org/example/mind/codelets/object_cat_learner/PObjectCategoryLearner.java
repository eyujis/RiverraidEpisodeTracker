package org.example.mind.codelets.object_cat_learner;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_cat_learner.entities.CategoryFactory;
import org.example.mind.codelets.object_cat_learner.entities.ObjectCategory;
import org.example.mind.codelets.object_cat_learner.entities.PObjectCategory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class PObjectCategoryLearner {
    private Idea objCategoryList;
    private CategoryFactory catFactory;

    double RELEVANCE_THRESHOLD = 5;
    double INIT_RELEVANCE = 1;
    double INCREMENT_FACTOR = 2.2;
    double DECREMENT_FACTOR = 0.5;
    double MINIMUM_RELEVANCE = 0.5;

    public PObjectCategoryLearner() {
        catFactory = new CategoryFactory();
        objCategoryList = new Idea("pCategories", "", 0);
    }

    public void updateCategories(Idea detectedObjects) {
        // find categories and increment relevance of existing categories
        for(Idea objectInstance : detectedObjects.getL()) {
            int assignedCatIdx = this.belongToCatIdx(objectInstance);

            if(assignedCatIdx == -1) {
                Idea newObjCategory = catFactory.createPCategory(objectInstance, INIT_RELEVANCE);
                this.objCategoryList.add(newObjCategory);
            } else {
                ObjectCategory cat = (ObjectCategory) this.objCategoryList.getL().get(assignedCatIdx).getValue();
                if(cat.getRelevance()<RELEVANCE_THRESHOLD) {
                    cat.incrementRelevance(INCREMENT_FACTOR);
                }
            }
        }

        decrementCategoriesRelevance();
        removeIrrelevantCategories();
    }

    public void removeIrrelevantCategories() {
        ArrayList<Integer> idxsToRemove = new ArrayList();

        for(int i=0; i<objCategoryList.getL().size(); i++) {
            Idea objCatIdea = objCategoryList.getL().get(i);
            ObjectCategory objCat = (ObjectCategory) objCatIdea.getValue();
            if(objCat.getRelevance() < MINIMUM_RELEVANCE) {
                idxsToRemove.add(i);
            }
        }

        Collections.sort(idxsToRemove, Collections.reverseOrder());

        for (int index : idxsToRemove) {
            if (index >= 0 && index < objCategoryList.getL().size()) {
                objCategoryList.getL().remove(index);
            }
        }

    }

    public void decrementCategoriesRelevance() {
        for(Idea objectCategory : objCategoryList.getL()) {
            PObjectCategory cat = (PObjectCategory) objectCategory.getValue();
            if(cat.getRelevance() < RELEVANCE_THRESHOLD) {
                cat.decrementRelevance(DECREMENT_FACTOR);
            }
        }
    }

    public int belongToCatIdx(Idea obj) {
        int idx = -1;

        for(int i=0; i<objCategoryList.getL().size(); i++) {
            ObjectCategory cat = (ObjectCategory) objCategoryList.getL().get(i).getValue();
            if(cat.membership(obj) == 1) {
                idx = i;
            }
        }
        return idx;
    }

    public Idea getRelevantCategories() {
        Idea relevantCategories = new Idea("pRelCategories", "", 0);

        for(Idea objectCategory : objCategoryList.getL()) {
            ObjectCategory cat = (ObjectCategory) objectCategory.getValue();
            if(cat.getRelevance()>=RELEVANCE_THRESHOLD) {
                relevantCategories.add(objectCategory);
            }
        }

        return relevantCategories;
    }
}
