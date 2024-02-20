package org.example.mind.codelets.object_proposer;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_cat_learner.entities.ObjectCategory;
import org.example.mind.codelets.object_proposer.entities.ObjectFactory;
import org.example.mind.codelets.object_proposer.entity_trackers.ObjectTracker;

import java.util.Optional;

public class ObjectProposer {
    private Idea unObjectsCF;
    private Idea idObjectsCF;

    private ObjectFactory objectFactory;
    private ObjectTracker objectTracker;
    private FragmentComparator fragmentComparator;

    private double MIN_CLUSTER_DISTANCE = 0;

    public ObjectProposer() {
        objectFactory = new ObjectFactory();
        objectTracker = new ObjectTracker();
        fragmentComparator = new FragmentComparator();

        unObjectsCF = new Idea("unObjsCF", "", 0);
        idObjectsCF = new Idea("idObjsCF", "", 0);
    }

    public void update(Idea detectedFragments, Idea objectCategories) {
        unObjectsCF = extractObjectInstances(detectedFragments, objectCategories);
        if(unObjectsCF.getL().size()>0) {
            idObjectsCF = objectTracker.identifyBetweenFrames(unObjectsCF);
        } else {
            idObjectsCF = new Idea("idObjsCF", "", 0);
        }
    }

    public Idea extractObjectInstances(Idea detectedFragments, Idea objectCategories) {

        Idea objectInstances = new Idea("objectInstances", "", 0);

        Idea fragmentClusters = extractFragmentClusters(detectedFragments);
        for(Idea fragmentCluster : fragmentClusters.getL()) {
            Optional<Idea> matchedCategory = objectCategories.getL().stream()
                    .filter(categoryIdea -> ((ObjectCategory) categoryIdea.getValue()).membership(fragmentCluster)==1)
                    .findFirst();

            if(matchedCategory.isPresent()) {
                Idea newUnObject = objectFactory.createUnObject(fragmentCluster, matchedCategory.get());
                if(newUnObject!=null) {
                    objectInstances.add(newUnObject);
                }
            }
        }
        return objectInstances;
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

    public Idea getUnObjectsCF() {
        return unObjectsCF;
    }

    public Idea getDetectedObjectsCF() {
        return idObjectsCF;
    }
}

