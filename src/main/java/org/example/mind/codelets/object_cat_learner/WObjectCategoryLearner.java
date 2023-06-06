package org.example.mind.codelets.object_cat_learner;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_cat_learner.entities.PObjectCategory;
import org.example.mind.codelets.object_cat_learner.entities.WObjectCategory;
import org.example.mind.codelets.object_proposer_codelet.ObjectComparator;

import java.util.*;

public class WObjectCategoryLearner {
    ArrayList<WObjectCategory> objCategoryList;
    ObjectComparator objectComparator = new ObjectComparator();

    double MIN_CLUSTER_DISTANCE = 2;

    double RELEVANCE_THRESHOLD = 5;
    double INIT_RELEVANCE = 1;
    double INCREMENT_FACTOR = 2.2;
    double DECREMENT_FACTOR = 0.5;
    double MINIMUM_RELEVANCE = 0.5;

    public WObjectCategoryLearner() {
        objCategoryList = new ArrayList<WObjectCategory>();
    }

    public void updateCategories(Idea detectedObjects) {
        ArrayList<WObjectCategory> rcvCategories = extractWCategories(detectedObjects);

        for(WObjectCategory rcvCat : rcvCategories) {
            int equalCatIdx = this.equalCategoryIdx(rcvCat);

            if(equalCatIdx == -1) {
                objCategoryList.add(rcvCat);
            } else {
                if(objCategoryList.get(equalCatIdx).getRelevance()<RELEVANCE_THRESHOLD) {
                    objCategoryList.get(equalCatIdx).incrementRelevance(INCREMENT_FACTOR);
                }
            }
        }

        decrementCategoriesRelevance();
        removeIrrelevantCategories();
    }

    public int equalCategoryIdx(WObjectCategory cat) {
        int idx = -1;

        for(int i=0; i<objCategoryList.size(); i++) {
            if(objCategoryList.get(i).equals(cat) == true) {
                idx = i;
            }
        }
        return idx;
    }

    public ArrayList<WObjectCategory> extractWCategories(Idea detectedObjects) {

        boolean[][] borderMatrix = initializeBooleanMatrix(detectedObjects.getL().size());

        for(int i=0; i<detectedObjects.getL().size(); i++) {
            for(int j=0; j<detectedObjects.getL().size(); j++) {
                Idea obj1 = detectedObjects.getL().get(i);
                Idea obj2 = detectedObjects.getL().get(j);

                if(i!=j && Math.abs(objectComparator.rectDistance(obj1, obj2))<=MIN_CLUSTER_DISTANCE
                  && obj1.get("category").getValue() != null
                  && obj2.get("category").getValue() != null) {
                    borderMatrix[i][j] = true;
                }
            }
        }

        boolean[] visited = new boolean[detectedObjects.getL().size()];
        for (int i = 0; i < visited.length; i++) {
            visited[i] = false;
        }

        ArrayList<ArrayList<PObjectCategory>> categoryClusters = new ArrayList<>();

        for (int i = 0; i < detectedObjects.getL().size(); i++) {
            if (!visited[i]) {
                ArrayList<PObjectCategory> group = new ArrayList<>();
                exploreBorders(i, borderMatrix, visited, group, detectedObjects);
                categoryClusters.add(group);
            }
        }

        ArrayList<WObjectCategory> wObjectCategories = new ArrayList<>();

        for(ArrayList<PObjectCategory> categoryCluster : categoryClusters) {
            if(categoryCluster.size()>1) {
                WObjectCategory wObjectCategory = new WObjectCategory(categoryCluster, INIT_RELEVANCE);
                wObjectCategories.add(wObjectCategory);
            }
        }

        return wObjectCategories;
    }

    private static void exploreBorders(int obj, boolean[][] borderMatrix, boolean[] visited, ArrayList<PObjectCategory> group, Idea objects) {
        visited[obj] = true;

        if(objects.getL().get(obj).get("category").getValue() != "null") {
            group.add((PObjectCategory) objects.getL().get(obj).get("category").getValue());
            for (int i = 0; i < borderMatrix[obj].length; i++) {
                if (borderMatrix[obj][i] && !visited[i]) {
                    exploreBorders(i, borderMatrix, visited, group, objects);
                }
            }
        }
    }


    public void removeIrrelevantCategories() {
        for(Iterator<WObjectCategory> iter = objCategoryList.iterator(); iter.hasNext(); ) {
            WObjectCategory objectCategory = iter.next();
            if(objectCategory.getRelevance() < MINIMUM_RELEVANCE) {
                iter.remove();
            }
        }
    }

    public void decrementCategoriesRelevance() {
        for(WObjectCategory objectCategory : objCategoryList) {
            if(objectCategory.getRelevance() < RELEVANCE_THRESHOLD) {
                objectCategory.decrementRelevance(DECREMENT_FACTOR);
            }
        }
    }


    public ArrayList<WObjectCategory> getRelevantCategories() {
        ArrayList<WObjectCategory> relevantCategories = new ArrayList<>();

        for(WObjectCategory objectCategory : objCategoryList) {
            if(objectCategory.getRelevance()>=RELEVANCE_THRESHOLD) {
                relevantCategories.add(objectCategory);
            }
        }
        return relevantCategories;
    }

    public boolean[][] initializeBooleanMatrix(int size) {
        boolean[][] matrix = new boolean[size][size];
        for(int i=0; i<matrix.length; i++) {
            for(int j=0; j<matrix[0].length; j++) {
                matrix[i][j] = false;
            }
        }
        return matrix;
    }
}
