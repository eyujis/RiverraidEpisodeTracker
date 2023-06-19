package org.example.mind.codelets.object_proposer_codelet;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_cat_learner.entities.FragmentCategory;
import org.example.mind.codelets.object_cat_learner.entities.ObjectCategory;
import org.example.mind.codelets.object_proposer_codelet.object_tracker.FragmentTracker;
import org.opencv.core.Mat;

public class ObjectProposer {

    private VSSketchpad vsSketchpad;
    private FragmentTracker fragmentTracker;
    private FragmentComparator fragmentComparator;

    private double MIN_CLUSTER_DISTANCE = 2;

    private Idea unFragsCF;
    private Idea idFragsCF;

    public ObjectProposer() {
        vsSketchpad = new VSSketchpad();
        fragmentTracker = new FragmentTracker();
        fragmentComparator = new FragmentComparator();
    }

    public void update(Mat frame) {
        unFragsCF = vsSketchpad.getUnFragmentsFromFrame(frame);
        idFragsCF = fragmentTracker.identifyBetweenFrames(unFragsCF);

    }
    public void assignFragmentCategories(Idea fragmentCategories) {
        for(Idea fragCatIdea : fragmentCategories.getL()) {
            FragmentCategory fragCat = (FragmentCategory) fragCatIdea.getValue();
            for (Idea idFrag : idFragsCF.getL()) {
                if (fragCat.membership(idFrag) == 1) {
                    idFrag.get("FragmentCategory").setValue(fragCatIdea.getName());
                }
            }
        }
    }

    public void assignObjectCategories(Idea objectCategories) {
        Idea fragmentClusters = extractFragmentClusters(getDetectedFragmentsCF());
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

                if(i!=j && Math.abs(fragmentComparator.rectDistance(f1, f2))<=MIN_CLUSTER_DISTANCE) {
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

    public Idea getUnFrags() {
        return unFragsCF;
    }

    public Idea getIdFragsCF() {
        return idFragsCF;
    }

    public Idea getDetectedFragmentsCF() {
        return idFragsCF;
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
