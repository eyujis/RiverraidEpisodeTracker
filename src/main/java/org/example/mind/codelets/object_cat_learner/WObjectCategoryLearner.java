package org.example.mind.codelets.object_cat_learner;

import org.example.mind.codelets.object_cat_learner.entities.PObjectCategory;
import org.example.mind.codelets.object_cat_learner.entities.WObjectCategory;
import org.example.mind.codelets.object_proposer_codelet.ObjectComparator;
import org.example.mind.codelets.object_proposer_codelet.entities.RRObject;

import java.util.*;

public class WObjectCategoryLearner {
    ArrayList<WObjectCategory> objCategoryList;
    ObjectComparator objectComparator = new ObjectComparator();

    double MIN_CLUSTER_DISTANCE = 0;

    double RELEVANCE_THRESHOLD = 5;
    double INIT_RELEVANCE = 1;
    double INCREMENT_FACTOR = 2.2;
    double DECREMENT_FACTOR = 0.5;
    double MINIMUM_RELEVANCE = 0.5;

    public WObjectCategoryLearner() {
        objCategoryList = new ArrayList<WObjectCategory>();
    }

    public void updateCategories(ArrayList<RRObject> objectInstances) {
        ArrayList<WObjectCategory> rcvCategories = extractWCategories(objectInstances);
//        System.out.println(rcvCategories.size());
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

    public ArrayList<WObjectCategory> extractWCategories(ArrayList<RRObject> objectInstances) {

        ArrayList<ArrayList<RRObject>> objectClusters = new ArrayList<>();


        boolean[][] borderMatrix = initializeBooleanMatrix(objectInstances.size());

        for(int i=0; i<objectInstances.size(); i++) {
            for(int j=0; j<objectInstances.size(); j++) {
                RRObject obj1 = objectInstances.get(i);
                RRObject obj2 = objectInstances.get(j);

                if(i!=j && objectComparator.rectDistance(obj1, obj2)<=MIN_CLUSTER_DISTANCE
                  && obj1.getAssignedCategory() != null
                  && obj2.getAssignedCategory() !=null) {
                    borderMatrix[i][j] = true;
                }
            }
        }

        boolean[] visited = new boolean[objectInstances.size()];
        for (int i = 0; i < visited.length; i++) {
            visited[i] = false;
        }

        ArrayList<ArrayList<PObjectCategory>> categoryClusters = new ArrayList<>();

        for (int i = 0; i < objectInstances.size(); i++) {
            if (!visited[i]) {
                ArrayList<PObjectCategory> group = new ArrayList<>();
                exploreBorders(i, borderMatrix, visited, group, objectInstances);
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

    private static void exploreBorders(int obj, boolean[][] borderMatrix, boolean[] visited, ArrayList<PObjectCategory> group, ArrayList<RRObject> objects) {
        visited[obj] = true;
        group.add(objects.get(obj).getAssignedCategory());

        for (int i = 0; i < borderMatrix[obj].length; i++) {
            if (borderMatrix[obj][i] && !visited[i]) {
                exploreBorders(i, borderMatrix, visited, group, objects);
            }
        }
    }



//    public int belongsToClusterIdx(ArrayList<ArrayList<RRObject>> objectClusters, RRObject objectInstance) {
//        for(int i=0; i<objectClusters.size(); i++) {
//            for(RRObject objFromCluster : objectClusters.get(i)) {
//                if(objectComparator.rectDistance(objectInstance, objFromCluster) <= MIN_CLUSTER_DISTANCE) {
//                    return i;
//                }
//            }
//        }
//        return -1;
//    }

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
        System.out.println("O1 P1:" + relevantCategories.get(0).getCatParts().get(0).getColorIdScalar());
        System.out.println("O1 P2:" + relevantCategories.get(0).getCatParts().get(1).getColorIdScalar());
        System.out.println("O2 P1:" + relevantCategories.get(1).getCatParts().get(0).getColorIdScalar());
        System.out.println("O2 P2:" + relevantCategories.get(1).getCatParts().get(1).getColorIdScalar());
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
