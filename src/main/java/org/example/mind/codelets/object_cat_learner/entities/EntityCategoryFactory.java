package org.example.mind.codelets.object_cat_learner.entities;

import br.unicamp.cst.representation.idea.Idea;

import java.util.ArrayList;

public class EntityCategoryFactory {
    static int factoryId = 0;

    public Idea createFragmentCategory(Idea obj, double relevance) {
        Idea category = new Idea("FragmentCategory"+ generateObjId(), new FragmentCategory(obj, relevance));
        return category;
    }
    public Idea createObjectCategory(ArrayList<String> categoryCluster, double relevance) {
        Idea category = new Idea("ObjectCategory"+ generateObjId(), new ObjectCategory(categoryCluster, relevance));
        return category;
    }


    public int generateObjId() {
        factoryId++;
        return factoryId;
    }
}
