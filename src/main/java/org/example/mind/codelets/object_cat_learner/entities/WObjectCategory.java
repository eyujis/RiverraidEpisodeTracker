package org.example.mind.codelets.object_cat_learner.entities;

import br.unicamp.cst.representation.idea.Idea;

import java.util.ArrayList;
import java.util.List;

public class WObjectCategory extends  ObjectCategory{
    ArrayList<String> catParts;

    public WObjectCategory(ArrayList<String> catParts, double relevance) {
        this.catParts = catParts;
        super.relevance = relevance;
    }

    public boolean allObjectsHaveCategoriesAssigned(Idea objectCluster) {
        for(Idea object : objectCluster.getL()) {
            if(object.get("pCategory").getValue() == "null") {
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
            ArrayList<String> objectCatIds = new ArrayList<>();
            ArrayList<String> partCatIds = new ArrayList<>();

            for(Idea obj : objectCluster.getL()) {
                String objCat = (String) obj.get("pCategory").getValue();
                objectCatIds.add(objCat);
            }

            for(String catPart: catParts) {
                partCatIds.add(catPart);
            }

            if(objectCatIds.containsAll(partCatIds) && partCatIds.containsAll(objectCatIds)) {
                return 1;
            }
        }

        return 0;
    }

    public ArrayList<String> getCatParts() {
        return catParts;
    }

    public boolean equals(WObjectCategory compWCat) {

        ArrayList<String> compWCatParts = compWCat.getCatParts();

        if(compWCatParts.size() == catParts.size()) {
            ArrayList<String> compPartCatIds = new ArrayList<>();
            ArrayList<String> partCatIds = new ArrayList<>();

            for(String compCatPart : compWCatParts) {
                compPartCatIds.add(compCatPart);
            }

            for(String catPart: catParts) {
                partCatIds.add(catPart);
            }

            if(compPartCatIds.containsAll(partCatIds) && partCatIds.containsAll(compPartCatIds)) {
                return true;
            }
        }
        return false;
    }
}

