package org.example.mind.codelets.object_cat_learner;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_cat_learner.entities.CategoryFactory;
import org.example.mind.codelets.object_cat_learner.entities.ObjectCategory;
import org.example.mind.codelets.object_cat_learner.entities.PObjectCategory;
import org.example.mind.codelets.object_cat_learner.entities.WObjectCategory;
import org.example.mind.codelets.object_proposer_codelet.ObjectComparator;

import java.util.*;

public class WObjectCategoryLearner {
    private CategoryFactory catFactory;
    Idea objCategoryList;
    ObjectComparator objectComparator = new ObjectComparator();

    double MIN_CLUSTER_DISTANCE = 2;
    double RELEVANCE_THRESHOLD = 5;
    double INIT_RELEVANCE = 1;
    double INCREMENT_FACTOR = 2.2;
    double DECREMENT_FACTOR = 0.5;
    double MINIMUM_RELEVANCE = 0.5;

    public WObjectCategoryLearner() {
        catFactory = new CategoryFactory();
        objCategoryList = new Idea("wCategories", "", 0);
    }

    public void updateCategories(Idea detectedObjects) {
        Idea rcvCategories = extractWCategories(detectedObjects);

        for(Idea rcvCat : rcvCategories.getL()) {
            int equalCatIdx = this.equalCategoryIdx(rcvCat);

            if(equalCatIdx == -1) {
                objCategoryList.add(rcvCat);
            } else {
                ObjectCategory objCat = (ObjectCategory) objCategoryList.getL().get(equalCatIdx).getValue();
                if(objCat.getRelevance()<RELEVANCE_THRESHOLD) {
                    objCat.incrementRelevance(INCREMENT_FACTOR);
                }
            }
        }

        decrementCategoriesRelevance();
        removeIrrelevantCategories();
    }

    public int equalCategoryIdx(Idea cat) {
        int idx = -1;

        for(int i=0; i<objCategoryList.getL().size(); i++) {
            WObjectCategory wObjCatListElem = (WObjectCategory) objCategoryList.getL().get(i).getValue();
            WObjectCategory wObjCatComp = (WObjectCategory) cat.getValue();
            if(wObjCatListElem.equals(wObjCatComp) == true) {
                idx = i;
            }
        }
        System.out.println(idx);
        return idx;
    }

    public Idea extractWCategories(Idea detectedObjects) {

        boolean[][] borderMatrix = initializeBooleanMatrix(detectedObjects.getL().size());

        for(int i=0; i<detectedObjects.getL().size(); i++) {
            for(int j=0; j<detectedObjects.getL().size(); j++) {
                Idea obj1 = detectedObjects.getL().get(i);
                Idea obj2 = detectedObjects.getL().get(j);

                if(i!=j
                  && Math.abs(objectComparator.rectDistance(obj1, obj2))<=MIN_CLUSTER_DISTANCE
                  && obj1.get("pCategory").getValue() != null
                  && obj2.get("pCategory").getValue() != null) {
                    borderMatrix[i][j] = true;
                }
            }
        }

        boolean[] visited = new boolean[detectedObjects.getL().size()];
        for (int i = 0; i < visited.length; i++) {
            visited[i] = false;
        }

        ArrayList<ArrayList<String>> categoryClusters = new ArrayList<>();

        for (int i = 0; i < detectedObjects.getL().size(); i++) {
            if (!visited[i]) {
                ArrayList<String> group = new ArrayList<>();
                exploreBorders(i, borderMatrix, visited, group, detectedObjects);
                categoryClusters.add(group);
            }
        }

        Idea wObjectCategories = new Idea("wExtractedCategories", "", 0);

        for(ArrayList<String> categoryCluster : categoryClusters) {
            if(categoryCluster.size()>1) {
                Idea wObjectCategory = catFactory.createWCategory(categoryCluster, INIT_RELEVANCE);
                wObjectCategories.add(wObjectCategory);
            }
        }

        return wObjectCategories;
    }

    private static void exploreBorders(int obj, boolean[][] borderMatrix, boolean[] visited, ArrayList<String> group, Idea objects) {
        visited[obj] = true;

        if(objects.getL().get(obj).get("pCategory").getValue() != "null") {
            group.add((String) objects.getL().get(obj).get("pCategory").getValue());
            for (int i = 0; i < borderMatrix[obj].length; i++) {
                if (borderMatrix[obj][i] && !visited[i]) {
                    exploreBorders(i, borderMatrix, visited, group, objects);
                }
            }
        }
    }


    public void removeIrrelevantCategories() {
        ArrayList<Integer> idxsToRemove = new ArrayList();

        for(int i=0; i<objCategoryList.getL().size(); i++) {
            Idea objCatIdea = objCategoryList.getL().get(i);
            ObjectCategory objCat = (ObjectCategory) objCatIdea.getValue();
            if(objCat.getRelevance() < MINIMUM_RELEVANCE) {
                idxsToRemove.add(i);
            }
        }

        Collections.sort(idxsToRemove, Collections.reverseOrder());

        for (int index : idxsToRemove) {
            if (index >= 0 && index < objCategoryList.getL().size()) {
                objCategoryList.getL().remove(index);
            }
        }
    }

    public void decrementCategoriesRelevance() {
        for(Idea objCatIdea: objCategoryList.getL()) {
            ObjectCategory objCat = (ObjectCategory) objCatIdea.getValue();
            if(objCat.getRelevance() < RELEVANCE_THRESHOLD) {
                objCat.decrementRelevance(DECREMENT_FACTOR);
            }
        }
    }

    public Idea getRelevantCategories() {
        Idea relevantCategories = new Idea("wRelCategories", "", 0);;

        for(Idea objCatIdea : objCategoryList.getL()) {
            ObjectCategory objCat = (ObjectCategory) objCatIdea.getValue();
            if(objCat.getRelevance()>=RELEVANCE_THRESHOLD) {
                relevantCategories.add(objCatIdea);
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
