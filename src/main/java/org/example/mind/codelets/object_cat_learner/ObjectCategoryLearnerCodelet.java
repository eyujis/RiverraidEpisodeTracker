package org.example.mind.codelets.object_cat_learner;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;

public class ObjectCategoryLearnerCodelet extends Codelet {

    Memory detectedObjectsMO;
    Memory fragmentCategoriesMO;
    Memory objectCategoriesMO;

    FragmentCategoryLearner fragmentCategoryLearner = new FragmentCategoryLearner();
    ObjectCategoryLearner objectCategoryLearner = new ObjectCategoryLearner();

    @Override
    public void accessMemoryObjects() {
        detectedObjectsMO=(MemoryObject)this.getInput("DETECTED_OBJECTS");
        fragmentCategoriesMO =(MemoryObject)this.getOutput("FRAGMENT_CATEGORIES");
        objectCategoriesMO =(MemoryObject)this.getOutput("OBJECT_CATEGORIES");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        if(detectedObjectsMO.getI() == "") {
            return;
        }
        Idea detectedObjects = (Idea) detectedObjectsMO.getI();

        fragmentCategoryLearner.updateCategories(detectedObjects);
        fragmentCategoriesMO.setI(fragmentCategoryLearner.getRelevantCategories());

        objectCategoryLearner.updateCategories(detectedObjects);
        objectCategoriesMO.setI(objectCategoryLearner.getRelevantCategories());

    }
}
