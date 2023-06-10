package org.example.mind.codelets.object_cat_learner;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

public class ObjectCategoryLearnerCodelet extends Codelet {

    Memory detectedObjectsMO;
    Memory objectPCategoriesMO;
    Memory objectWCategoriesMO;

    PObjectCategoryLearner pObjectCategoryLearner = new PObjectCategoryLearner();
    WObjectCategoryLearner wObjectCategoryLearner = new WObjectCategoryLearner();

    @Override
    public void accessMemoryObjects() {
        detectedObjectsMO=(MemoryObject)this.getInput("DETECTED_OBJECTS");
        objectPCategoriesMO=(MemoryObject)this.getOutput("OBJECT_PCATEGORIES");
        objectWCategoriesMO=(MemoryObject)this.getOutput("OBJECT_WCATEGORIES");
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

        pObjectCategoryLearner.updateCategories(detectedObjects);
        objectPCategoriesMO.setI(pObjectCategoryLearner.getRelevantCategories());

        wObjectCategoryLearner.updateCategories(detectedObjects);
        objectWCategoriesMO.setI(wObjectCategoryLearner.getRelevantCategories());

    }
}
