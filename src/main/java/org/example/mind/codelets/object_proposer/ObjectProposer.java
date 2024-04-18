package org.example.mind.codelets.object_proposer;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_cat_learner.entities.EntityCategoryFactory;
import org.example.mind.codelets.object_cat_learner.entities.ObjectCategory;
import org.example.mind.codelets.object_proposer.entities.ObjectFactory;
import org.example.mind.codelets.object_proposer.entity_trackers.ObjectTracker;
import org.example.mind.codelets.object_proposer.object_label.FragmentRGB2ColorLabel;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class ObjectProposer {
    private Idea unObjectsCF;
    private Idea idObjectsCF;

    Idea assimilatedCategories;

    private ObjectFactory objectFactory;
    private ObjectTracker objectTracker;
    private FragmentComparator fragmentComparator;
    private EntityCategoryFactory catFactory;

    double INIT_RELEVANCE = 1;

    public ObjectProposer() {
        objectFactory = new ObjectFactory();
        objectTracker = new ObjectTracker();
        fragmentComparator = new FragmentComparator();
        catFactory = new EntityCategoryFactory();

        unObjectsCF = new Idea("unObjsCF", "", 0);
        idObjectsCF = new Idea("idObjsCF", "", 0);
    }

    public void update(Idea detectedFragments, Idea objectCategories) {
        assimilatedCategories = new Idea("AssimilatedCategories", "", 0);

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
            } else {
                ArrayList<String> fragCatCluster = (ArrayList<String>) fragmentCluster.getL()
                        .stream()
                        .map(fragment -> (String) fragment.get("FragmentCategory").getValue())
                        .collect(Collectors.toList());

                Idea assimilatedObjectCategory = catFactory.createObjectCategory(fragCatCluster, INIT_RELEVANCE);
                Idea newUnObject = objectFactory.createUnObject(fragmentCluster, assimilatedObjectCategory);
                if(newUnObject!=null) {
                    objectInstances.add(newUnObject);
                }
                assimilatedCategories.getL().add(assimilatedObjectCategory);
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

                FragmentRGB2ColorLabel fragmentRGB2ColorLabel = new FragmentRGB2ColorLabel();
                String f1Color = fragmentRGB2ColorLabel.getColorLabel(f1.get("color"));
                String f2Color = fragmentRGB2ColorLabel.getColorLabel(f2.get("color"));
                boolean f1IsYellow = f1Color != null ? f1Color.equals("shipOrMissileYellow"): false;
                boolean f2IsYellow = f2Color != null ? f2Color.equals("shipOrMissileYellow"): false;

                if(i!=j && fragmentComparator.rectDistance(f1, f2)
                        && !f1IsYellow
                        && !f2IsYellow) {
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

    public Idea getAssimilatedCategories() {
        return assimilatedCategories;
    }

    public Idea getUnObjectsCF() {
        return unObjectsCF;
    }

    public Idea getDetectedObjectsCF() {
        return idObjectsCF;
    }
}

