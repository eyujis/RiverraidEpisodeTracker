package org.example.mind.codelets.object_cat_learner.entities;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_cat_learner.WObjectCategoryLearner;
import org.example.mind.codelets.object_proposer_codelet.entities.IdentifiedRRObject;
import org.example.mind.codelets.object_proposer_codelet.entities.RRObject;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WObjectCategory extends  ObjectCategory{
    ArrayList<PObjectCategory> catParts;

    public WObjectCategory(ArrayList<PObjectCategory> catParts, double relevance) {
        this.catParts = catParts;
        super.relevance = relevance;

        super.initializeCategoryId();
        super.initializeColorId();
    }

    public boolean allObjectsHaveCategoriesAssigned(Idea objectCluster) {
        for(Idea object : objectCluster.getL()) {
            if(object.get("category").getValue() == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Idea getInstance(List<Idea> constraints) {
        return null;
    }

    @Override
    public double membership(Idea objectCluster) {
        if(!allObjectsHaveCategoriesAssigned(objectCluster)) {
            return 0;
        }

        if(objectCluster.getL().size() == catParts.size()) {
            ArrayList<Integer> objectCatIds = new ArrayList<>();
            ArrayList<Integer> partCatIds = new ArrayList<>();

            for(Idea obj : objectCluster.getL()) {
                ObjectCategory objCat = (ObjectCategory) obj.get("category").getValue();
                objectCatIds.add((Integer) objCat.getCategoryId());
            }

            for(PObjectCategory catPart: catParts) {
                partCatIds.add((Integer) catPart.getCategoryId());
            }

            if(objectCatIds.containsAll(partCatIds) && partCatIds.containsAll(objectCatIds)) {
                return 1;
            }
        }

        return 0;
    }

    public ArrayList<PObjectCategory> getCatParts() {
        return catParts;
    }

    public boolean equals(WObjectCategory compWCat) {

        ArrayList<PObjectCategory> compWCatParts = compWCat.getCatParts();

        if(compWCatParts.size() == catParts.size()) {
            ArrayList<Integer> compPartCatIds = new ArrayList<>();
            ArrayList<Integer> partCatIds = new ArrayList<>();

            for(PObjectCategory compCatPart : compWCatParts) {
                compPartCatIds.add(compCatPart.getCategoryId());
            }

            for(PObjectCategory catPart: catParts) {
                partCatIds.add(catPart.getCategoryId());
            }

            if(compPartCatIds.containsAll(partCatIds) && partCatIds.containsAll(compPartCatIds)) {
                return true;
            }
        }
        return false;
    }
}

