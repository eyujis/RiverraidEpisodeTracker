package org.example.mind.codelets.object_cat_learner;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_cat_learner.entities.EntityCategoryFactory;
import org.example.mind.codelets.object_cat_learner.entities.EntityCategory;
import org.example.mind.codelets.object_cat_learner.entities.FragmentCategory;

import java.util.ArrayList;
import java.util.Collections;

public class FragmentCategoryLearner {
    private Idea fragCategoryList;
    private EntityCategoryFactory catFactory;

//    double RELEVANCE_THRESHOLD = 5;
    //IT REMEMBER ALL LEARNED FRAGS WITHOUT FORGETTING
    double RELEVANCE_THRESHOLD = 1;
    double INIT_RELEVANCE = 1;
    double INCREMENT_FACTOR = 2.2;
    double DECREMENT_FACTOR = 0.7;
    double MINIMUM_RELEVANCE = 0.5;

    public FragmentCategoryLearner() {
        catFactory = new EntityCategoryFactory();
        fragCategoryList = new Idea("FragmentCategories", "", 0);
    }

    public void updateCategories(Idea detectedFragments) {
        // find categories and increment relevance of existing categories
        for(Idea fragmentInstance : detectedFragments.getL()) {
            int assignedCatIdx = this.belongToCatIdx(fragmentInstance);

            if(assignedCatIdx == -1) {
                Idea newFragCategory = catFactory.createFragmentCategory(fragmentInstance, INIT_RELEVANCE);
                this.fragCategoryList.add(newFragCategory);
            } else {
                EntityCategory cat = (EntityCategory) this.fragCategoryList.getL().get(assignedCatIdx).getValue();
                if(cat.getRelevance()<RELEVANCE_THRESHOLD) {
                    cat.incrementRelevance(INCREMENT_FACTOR);
                }
            }
        }

//        decrementCategoriesRelevance();
//        removeIrrelevantCategories();
    }

    public void removeIrrelevantCategories() {
        ArrayList<Integer> idxsToRemove = new ArrayList();

        for(int i = 0; i< fragCategoryList.getL().size(); i++) {
            Idea fragCatIdea = fragCategoryList.getL().get(i);
            EntityCategory fragCat = (EntityCategory) fragCatIdea.getValue();
            if(fragCat.getRelevance() < MINIMUM_RELEVANCE) {
                idxsToRemove.add(i);
            }
        }

        Collections.sort(idxsToRemove, Collections.reverseOrder());

        for (int index : idxsToRemove) {
            if (index >= 0 && index < fragCategoryList.getL().size()) {
                fragCategoryList.getL().remove(index);
            }
        }

    }

    public void decrementCategoriesRelevance() {
        for(Idea fragmentCategory : fragCategoryList.getL()) {
            FragmentCategory cat = (FragmentCategory) fragmentCategory.getValue();
            if(cat.getRelevance() < RELEVANCE_THRESHOLD) {
                cat.decrementRelevance(DECREMENT_FACTOR);
            }
        }
    }

    public int belongToCatIdx(Idea frag) {
        int idx = -1;

        for(int i = 0; i< fragCategoryList.getL().size(); i++) {
            EntityCategory cat = (EntityCategory) fragCategoryList.getL().get(i).getValue();
            if(cat.membership(frag) == 1) {
                idx = i;
            }
        }
        return idx;
    }

    public Idea getRelevantCategories() {
        Idea relevantCategories = new Idea("pRelCategories", "", 0);

        for(Idea fragmentCategory : fragCategoryList.getL()) {
            EntityCategory cat = (EntityCategory) fragmentCategory.getValue();
            if(cat.getRelevance()>=RELEVANCE_THRESHOLD) {
                relevantCategories.add(fragmentCategory);
            }
        }

        return relevantCategories;
    }
}
