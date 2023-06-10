package org.example.mind.codelets.object_cat_learner.entities;

import br.unicamp.cst.representation.idea.Idea;
import org.opencv.core.Scalar;

import java.util.ArrayList;

public class CategoryFactory {
    static int factoryId = 0;

    public Idea createPCategory(Idea obj, double relevance) {
        Idea category = new Idea("pCategory"+ generateObjId(), new PObjectCategory(obj, relevance));
        return category;
    }
    public Idea createWCategory(ArrayList<String> categoryCluster, double relevance) {
        Idea category = new Idea("wCategory"+ generateObjId(), new WObjectCategory(categoryCluster, relevance));
        return category;
    }


    public int generateObjId() {
        factoryId++;
        return factoryId;
    }
}
