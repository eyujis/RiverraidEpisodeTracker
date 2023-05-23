package org.example.mind.codelets.object_cat_learner;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import org.example.mind.codelets.object_proposer_codelet.entities.RRObject;

import java.util.ArrayList;

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
        ArrayList<RRObject> idObjs = (ArrayList<RRObject>) detectedObjectsMO.getI();

        pObjectCategoryLearner.updateCategories(idObjs);
        objectPCategoriesMO.setI(pObjectCategoryLearner.getRelevantCategories());

        wObjectCategoryLearner.updateCategories(idObjs);
        objectWCategoriesMO.setI(wObjectCategoryLearner.getRelevantCategories());

    }
}
