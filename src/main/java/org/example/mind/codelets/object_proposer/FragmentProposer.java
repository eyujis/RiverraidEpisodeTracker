package org.example.mind.codelets.object_proposer;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_cat_learner.entities.EntityCategoryFactory;
import org.example.mind.codelets.object_cat_learner.entities.FragmentCategory;
import org.example.mind.codelets.object_cat_learner.entities.ObjectCategory;
import org.example.mind.codelets.object_proposer.entities.FragmentFactory;
import org.example.mind.codelets.object_proposer.entity_trackers.FragmentTracker;
import org.opencv.core.Mat;

import java.util.Optional;

public class FragmentProposer {

    private VSSketchpad vsSketchpad;
    private FragmentTracker fragmentTracker;
    private FragmentComparator fragmentComparator;
    FragmentFactory fragmentFactory;
    EntityCategoryFactory entityCategoryFactory;

    int INIT_RELEVANCE = 1;

    private Idea possibleFragments;
    private Idea unFragsCF;
    private Idea idFragsCF;

    public FragmentProposer() {
        vsSketchpad = new VSSketchpad();
        fragmentFactory = new FragmentFactory();
        entityCategoryFactory = new EntityCategoryFactory();
        fragmentTracker = new FragmentTracker();
        fragmentComparator = new FragmentComparator();
    }

    public void update(Mat frame, Idea fragmentCategories) {
        possibleFragments = vsSketchpad.getUnFragmentsFromFrame(frame, fragmentCategories);

        unFragsCF = new Idea("DetectedFragments", "", 0);

//        //Assigning and assimilating categories
        for(Idea possibleFragment: possibleFragments.getL()) {
            Optional<Idea> matchedCategory = fragmentCategories.getL().stream()
                    .filter(categoryIdea -> ((FragmentCategory) categoryIdea.getValue()).membership(possibleFragment)==1)
                    .findFirst();

            if(matchedCategory.isPresent()) {
                possibleFragment.get("FragmentCategory").setValue(matchedCategory.get().getName());
                unFragsCF.add(possibleFragment);
            } else {
                Idea assimilatedFragmentCategory = entityCategoryFactory.createFragmentCategory(possibleFragment, INIT_RELEVANCE);
                possibleFragment.get("FragmentCategory").setValue(assimilatedFragmentCategory.getName());
                unFragsCF.add(possibleFragment);

                fragmentCategories.getL().add(assimilatedFragmentCategory);
            }
        }

        idFragsCF = fragmentTracker.identifyBetweenFrames(unFragsCF);
    }

    public void assignObjectCategories(Idea objectCategories) {
        Idea fragmentClusters = extractFragmentClusters(idFragsCF);
        for(Idea fragmentCluster : fragmentClusters.getL()) {
            for(Idea objCatIdea : objectCategories.getL()) {
                ObjectCategory objCat = (ObjectCategory) objCatIdea.getValue();
                if(objCat.membership(fragmentCluster) == 1) {
                    for(Idea obj: fragmentCluster.getL()) {
                        Idea ObjCat = obj.get("ObjectCategory");
                        ObjCat.setValue(objCatIdea.getName());
                    }
                }
            }
        }
    }

    public Idea extractFragmentClusters(Idea fragmentInstances) {

        boolean[][] borderMatrix = initializeBooleanMatrix(fragmentInstances.getL().size());

        for(int i=0; i<fragmentInstances.getL().size(); i++) {
            for(int j=0; j<fragmentInstances.getL().size(); j++) {
                Idea f1 = fragmentInstances.getL().get(i);
                Idea f2 = fragmentInstances.getL().get(j);

                if(i!=j && fragmentComparator.rectDistance(f1, f2)) {
                    borderMatrix[i][j] = true;
                }
            }
        }

        boolean[] visited = new boolean[fragmentInstances.getL().size()];
        for (int i = 0; i < visited.length; i++) {
            visited[i] = false;
        }

        Idea fragmentClusters = new Idea("fragmentClusters", "", 0);

        for (int i = 0; i < fragmentInstances.getL().size(); i++) {
            if (!visited[i]) {
                Idea fragmentCluster = new Idea("fragmentCluster", "", 0);
                exploreFragmentBorders(i, borderMatrix, visited, fragmentCluster, fragmentInstances);
                fragmentClusters.add(fragmentCluster);
            }
        }


        return fragmentClusters;
    }

    private static void exploreFragmentBorders(int fragIdx, boolean[][] borderMatrix, boolean[] visited, Idea fragmentCluster, Idea fragments) {
        visited[fragIdx] = true;
        fragmentCluster.add(fragments.getL().get(fragIdx));

        for (int i = 0; i < borderMatrix[fragIdx].length; i++) {
            if (borderMatrix[fragIdx][i] && !visited[i]) {
                exploreFragmentBorders(i, borderMatrix, visited, fragmentCluster, fragments);
            }
        }
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

    public Idea getUnFrags() {
        return unFragsCF;
    }

    public Idea getDetectedFragmentsCF() {
        return idFragsCF;
    }
}
