package org.example.mind.codelets.object_cat_learner;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_cat_learner.entities.EntityCategoryFactory;
import org.example.mind.codelets.object_cat_learner.entities.EntityCategory;
import org.example.mind.codelets.object_cat_learner.entities.ObjectCategory;
import org.example.mind.codelets.object_proposer.FragmentComparator;

import java.util.*;

public class ObjectCategoryLearner {
    private EntityCategoryFactory catFactory;
    Idea objCategoryList;
    FragmentComparator fragmentComparator = new FragmentComparator();

    double MIN_CLUSTER_DISTANCE = 2;
    double RELEVANCE_THRESHOLD = 5;
    double INIT_RELEVANCE = 1;
    double INCREMENT_FACTOR = 2.2;
    double DECREMENT_FACTOR = 0.7;
    double MINIMUM_RELEVANCE = 0.5;

    // TODO refactor this whole class, the code is too confusing;
    public ObjectCategoryLearner() {
        catFactory = new EntityCategoryFactory();
        objCategoryList = new Idea("ObjectCategories", "", 0);
    }

    public void updateCategories(Idea detectedFragments) {
        Idea rcvCategories = extractObjectCategories(detectedFragments);

        for(Idea rcvCat : rcvCategories.getL()) {
            int equalCatIdx = this.equalCategoryIdx(rcvCat);

            if(equalCatIdx == -1) {
                objCategoryList.add(rcvCat);
            } else {
                EntityCategory objCat = (EntityCategory) objCategoryList.getL().get(equalCatIdx).getValue();
                if(objCat.getRelevance()<RELEVANCE_THRESHOLD) {
                    objCat.incrementRelevance(INCREMENT_FACTOR);
                }
            }
        }

        decrementCategoriesRelevance();
        removeSubsetCategories();
        removeIrrelevantCategories();
    }

    public int equalCategoryIdx(Idea cat) {
        int idx = -1;

        for(int i=0; i<objCategoryList.getL().size(); i++) {
            ObjectCategory objCatListElem = (ObjectCategory) objCategoryList.getL().get(i).getValue();
            ObjectCategory objCatComp = (ObjectCategory) cat.getValue();

            if(objCatListElem.equals(objCatComp) == true) {
                idx = i;
            }
        }
        return idx;
    }

    public Idea extractObjectCategories(Idea detectedFragments) {

        boolean[][] borderMatrix = initializeBooleanMatrix(detectedFragments.getL().size());

        for(int i=0; i<detectedFragments.getL().size(); i++) {
            for(int j=0; j<detectedFragments.getL().size(); j++) {
                Idea f1 = detectedFragments.getL().get(i);
                Idea f2 = detectedFragments.getL().get(j);

                if(i!=j
                  && Math.abs(fragmentComparator.rectDistance(f1, f2))<=MIN_CLUSTER_DISTANCE
                  && f1.get("FragmentCategory").getValue() != null
                  && f2.get("FragmentCategory").getValue() != null) {
                    borderMatrix[i][j] = true;
                }
            }
        }

        boolean[] visited = new boolean[detectedFragments.getL().size()];
        for (int i = 0; i < visited.length; i++) {
            visited[i] = false;
        }

        ArrayList<ArrayList<String>> fragCatClusters = new ArrayList<>();

        for (int i = 0; i < detectedFragments.getL().size(); i++) {
            if (!visited[i]) {
                ArrayList<String> group = new ArrayList<>();
                exploreBorders(i, borderMatrix, visited, group, detectedFragments);

                if(!group.isEmpty()) {
                    fragCatClusters.add(group);
                }
            }
        }

        Idea objectCategories = new Idea("extractedObjectCategories", "", 0);

        for(ArrayList<String> fragCatCluster : fragCatClusters) {
                Idea objectCategory = catFactory.createObjectCategory(fragCatCluster, INIT_RELEVANCE);
                objectCategories.add(objectCategory);
        }

        return objectCategories;
    }

    private static void exploreBorders(int fragIdx, boolean[][] borderMatrix, boolean[] visited, ArrayList<String> fragCluster, Idea fragments) {
        visited[fragIdx] = true;

        if(fragments.getL().get(fragIdx).get("FragmentCategory").getValue() != "null") {
            fragCluster.add((String) fragments.getL().get(fragIdx).get("FragmentCategory").getValue());
            for (int i = 0; i < borderMatrix[fragIdx].length; i++) {
                if (borderMatrix[fragIdx][i] && !visited[i]) {
                    exploreBorders(i, borderMatrix, visited, fragCluster, fragments);
                }
            }
        }
    }


    public void removeIrrelevantCategories() {
        ArrayList<Integer> idxsToRemove = new ArrayList();

        for(int i=0; i<objCategoryList.getL().size(); i++) {
            Idea objCatIdea = objCategoryList.getL().get(i);
            EntityCategory objCat = (EntityCategory) objCatIdea.getValue();
            if(objCat.getRelevance() < MINIMUM_RELEVANCE) {
                idxsToRemove.add(i);
            }
        }

        removeIdxFromObjectCategoryList(idxsToRemove);
    }

    public void removeSubsetCategories() {
        ArrayList<Integer> idxsToRemove = new ArrayList();

        for(int i=0; i<objCategoryList.getL().size(); i++) {
            for(int j=0; j<objCategoryList.getL().size(); j++) {
                if(i!=j) {
                    ObjectCategory objCat1 = (ObjectCategory) objCategoryList.getL().get(i).getValue();
                    ObjectCategory objCat2 = (ObjectCategory) objCategoryList.getL().get(j).getValue();

                    if(objCat1.getRelevance()>RELEVANCE_THRESHOLD
                       && objCat2.getRelevance()>RELEVANCE_THRESHOLD
                       && objCat1.getFragsCategories().size() > objCat2.getFragsCategories().size()
                       && objCat1.getFragsCategories().containsAll(objCat2.getFragsCategories())) {
                        idxsToRemove.add(j);
                    }
                }
            }
        }

        removeIdxFromObjectCategoryList(idxsToRemove);
    }

    private void removeIdxFromObjectCategoryList(ArrayList<Integer> idxsToRemove) {
        Collections.sort(idxsToRemove, Collections.reverseOrder());

        for (int index : idxsToRemove) {
            if (index >= 0 && index < objCategoryList.getL().size()) {
                objCategoryList.getL().remove(index);
            }
        }
    }

    public void decrementCategoriesRelevance() {
        for(Idea objCatIdea: objCategoryList.getL()) {
            EntityCategory objCat = (EntityCategory) objCatIdea.getValue();
            if(objCat.getRelevance() < RELEVANCE_THRESHOLD) {
                objCat.decrementRelevance(DECREMENT_FACTOR);
            }
        }
    }

    public Idea getRelevantCategories() {
        Idea relevantCategories = new Idea("RelevantCategories", "", 0);

        for(Idea objCatIdea : objCategoryList.getL()) {
            EntityCategory objCat = (EntityCategory) objCatIdea.getValue();
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
