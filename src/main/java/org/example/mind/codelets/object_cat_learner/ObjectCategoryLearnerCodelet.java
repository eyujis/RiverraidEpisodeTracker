package org.example.mind.codelets.object_cat_learner;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;

public class ObjectCategoryLearnerCodelet extends Codelet {

    Memory detectedFragmentsMO;
    Memory fragmentCategoriesMO;
    Memory objectCategoriesMO;

    FragmentCategoryLearner fragmentCategoryLearner = new FragmentCategoryLearner();
    ObjectCategoryLearner objectCategoryLearner = new ObjectCategoryLearner();

    @Override
    public void accessMemoryObjects() {
        detectedFragmentsMO =(MemoryObject)this.getInput("DETECTED_FRAGMENTS");
        fragmentCategoriesMO =(MemoryObject)this.getOutput("FRAGMENT_CATEGORIES");
        objectCategoriesMO =(MemoryObject)this.getOutput("OBJECT_CATEGORIES");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        if(detectedFragmentsMO.getI() == "") {
            return;
        }

        synchronized (objectCategoriesMO) {
            Idea detectedFragments = (Idea) detectedFragmentsMO.getI();

            fragmentCategoryLearner.updateCategories(detectedFragments);
            fragmentCategoriesMO.setI(fragmentCategoryLearner.getRelevantCategories());

            Idea objectCategories = null;
            if(objectCategoriesMO.getI()=="") {
                objectCategories = new Idea("ObjectCategories", "", 0);
            } else {
                objectCategories = (Idea) objectCategoriesMO.getI();
            }
            objectCategories = objectCategoryLearner.updateCategories(detectedFragments, objectCategories);
            objectCategoriesMO.setI(objectCategories);
        }
    }
}
