package org.example.mind.codelets.object_cat_learner.entities;

import br.unicamp.cst.representation.idea.Idea;

import java.util.ArrayList;
import java.util.List;

public class ObjectCategory extends EntityCategory {
    ArrayList<String> fragsCategories;

    public ObjectCategory(ArrayList<String> fragsCategories, double relevance) {
        this.fragsCategories = fragsCategories;
        super.relevance = relevance;
    }

    public boolean allFragmentsHaveCategoriesAssigned(Idea fragmentCluster) {
        for(Idea fragment : fragmentCluster.getL()) {
            if(fragment.get("FragmentCategory").getValue() == "null") {
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
    public double membership(Idea fragmentCluster) {
        if(!allFragmentsHaveCategoriesAssigned(fragmentCluster)) {
            return 0;
        }

        if(fragmentCluster.getL().size() == fragsCategories.size()) {
            ArrayList<String> clusterCatsIds = new ArrayList<>();
            ArrayList<String> fragsCatsIds = new ArrayList<>();

            for(Idea fragment : fragmentCluster.getL()) {
                String fragmentCat = (String) fragment.get("FragmentCategory").getValue();
                clusterCatsIds.add(fragmentCat);
            }

            for(String fragCategory: fragsCategories) {
                fragsCatsIds.add(fragCategory);
            }

            if(clusterCatsIds.containsAll(fragsCatsIds) && fragsCatsIds.containsAll(clusterCatsIds)) {
                return 1;
            }
        }

        return 0;
    }

    public ArrayList<String> getFragsCategories() {
        return fragsCategories;
    }

    public boolean equals(ObjectCategory compCat) {

        ArrayList<String> compCatFrags = compCat.getFragsCategories();

        if(compCatFrags.size() == fragsCategories.size()) {
            ArrayList<String> compPartCatIds = new ArrayList<>();
            ArrayList<String> partCatIds = new ArrayList<>();

            for(String compCatPart : compCatFrags) {
                compPartCatIds.add(compCatPart);
            }

            for(String catPart: fragsCategories) {
                partCatIds.add(catPart);
            }

            if(compPartCatIds.containsAll(partCatIds) && partCatIds.containsAll(compPartCatIds)) {
                return true;
            }
        }
        return false;
    }
}

