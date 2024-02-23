package org.example.mind.codelets.object_cat_learner;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_cat_learner.entities.EntityCategoryFactory;
import org.example.mind.codelets.object_cat_learner.entities.EntityCategory;
import org.example.mind.codelets.object_cat_learner.entities.FragmentCategory;

import java.util.ArrayList;
import java.util.Collections;

public class FragmentCategoryLearner {
    private EntityCategoryFactory catFactory;

    double RELEVANCE_THRESHOLD = 5;
    double INCREMENT_FACTOR = 2.2;
    double DECREMENT_FACTOR = 0.7;
    double MINIMUM_RELEVANCE = 0.5;

    public FragmentCategoryLearner() {
        catFactory = new EntityCategoryFactory();
    }

    public Idea updateCategories(Idea detectedFragments, Idea fragmentCategories) {
        // find categories and increment relevance of existing categories
        for(Idea fragmentInstance : detectedFragments.getL()) {
            int assignedCatIdx = this.belongToCatIdx(fragmentInstance, fragmentCategories);

            if(assignedCatIdx == -1) {
//                Idea newFragCategory = catFactory.createFragmentCategory(fragmentInstance, INIT_RELEVANCE);
//                fragmentCategories.getL().add(newFragCategory);
            } else {
                EntityCategory cat = (EntityCategory) fragmentCategories.getL().get(assignedCatIdx).getValue();
                if(cat.getRelevance()<RELEVANCE_THRESHOLD) {
                    cat.incrementRelevance(INCREMENT_FACTOR);
                }
            }
        }

        decrementCategoriesRelevance(fragmentCategories);
        removeIrrelevantCategories(fragmentCategories);

        return fragmentCategories;
    }

    public void removeIrrelevantCategories(Idea fragmentCategories) {
        ArrayList<Integer> idxsToRemove = new ArrayList();

        for(int i = 0; i< fragmentCategories.getL().size(); i++) {
            Idea fragCatIdea = fragmentCategories.getL().get(i);
            EntityCategory fragCat = (EntityCategory) fragCatIdea.getValue();
            if(fragCat.getRelevance() < MINIMUM_RELEVANCE) {
                idxsToRemove.add(i);
            }
        }

        Collections.sort(idxsToRemove, Collections.reverseOrder());

        for (int index : idxsToRemove) {
            if (index >= 0 && index < fragmentCategories.getL().size()) {
                fragmentCategories.getL().remove(index);
            }
        }

    }

    public void decrementCategoriesRelevance(Idea fragmentCategories) {
        for(Idea fragmentCategory : fragmentCategories.getL()) {
            FragmentCategory cat = (FragmentCategory) fragmentCategory.getValue();
            if(cat.getRelevance() < RELEVANCE_THRESHOLD) {
                cat.decrementRelevance(DECREMENT_FACTOR);
            }
        }
    }

    public int belongToCatIdx(Idea frag, Idea fragmentCategories) {
        int idx = -1;

        for(int i = 0; i< fragmentCategories.getL().size(); i++) {
            EntityCategory cat = (EntityCategory) fragmentCategories.getL().get(i).getValue();
            if(cat.membership(frag) == 1) {
                idx = i;
            }
        }
        return idx;
    }
}
