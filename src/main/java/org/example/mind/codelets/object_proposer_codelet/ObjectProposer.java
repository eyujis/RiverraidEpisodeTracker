package org.example.mind.codelets.object_proposer_codelet;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_cat_learner.entities.ObjectCategory;
import org.example.mind.codelets.object_cat_learner.entities.PObjectCategory;
import org.example.mind.codelets.object_cat_learner.entities.WObjectCategory;
import org.example.mind.codelets.object_proposer_codelet.object_tracker.ObjectTracker;
import org.opencv.core.Mat;

import java.util.ArrayList;

public class ObjectProposer {

    private VSSketchpad vsSketchpad;
    private ObjectTracker objTracker;
    private ObjectComparator objectComparator;

    private double MIN_CLUSTER_DISTANCE = 2;

    private Idea unObjsCF;
    private Idea idObjsCF;

    public ObjectProposer() {
        vsSketchpad = new VSSketchpad();
        objTracker = new ObjectTracker();
        objectComparator = new ObjectComparator();
    }

    public void update(Mat frame) {
        unObjsCF = vsSketchpad.getUnObjectsFromFrame(frame);
        idObjsCF = objTracker.identifyBetweenFrames(unObjsCF);

    }
    public void assignPCategories(ArrayList<PObjectCategory> objectCategories) {

        for(PObjectCategory objCat : objectCategories) {
            for (Idea idObj : idObjsCF.getL()) {
                if (objCat.membership(idObj) == 1) {
                    idObj.get("category").setValue(objCat);
                }
            }
        }
    }

    public void assignWCategories(ArrayList<WObjectCategory> objectCategories) {
        Idea objectClusters = extractObjectClusters(getDetectedObjectsCF());
        for(Idea objectCluster : objectClusters.getL()) {
            for(WObjectCategory objectCategory : objectCategories) {
                if(objectCategory.membership(objectCluster) == 1) {
                    for(Idea obj: objectCluster.getL()) {
                        ObjectCategory objCat = (ObjectCategory) obj.get("category").getValue();
                        objCat.setColorIdScalar(objectCategory.getColorIdScalar());
                    }
                }
            }
        }
    }

    public Idea extractObjectClusters(Idea objectInstances) {

        boolean[][] borderMatrix = initializeBooleanMatrix(objectInstances.getL().size());

        for(int i=0; i<objectInstances.getL().size(); i++) {
            for(int j=0; j<objectInstances.getL().size(); j++) {
                Idea obj1 = objectInstances.getL().get(i);
                Idea obj2 = objectInstances.getL().get(j);

                if(i!=j && Math.abs(objectComparator.rectDistance(obj1, obj2))<=MIN_CLUSTER_DISTANCE) {
                    borderMatrix[i][j] = true;
                }
            }
        }

        boolean[] visited = new boolean[objectInstances.getL().size()];
        for (int i = 0; i < visited.length; i++) {
            visited[i] = false;
        }

        Idea objectClusters = new Idea("objectClusters", "", 0);

        for (int i = 0; i < objectInstances.getL().size(); i++) {
            if (!visited[i]) {
                Idea group = new Idea("group", "", 0);
                exploreBorders(i, borderMatrix, visited, group, objectInstances);
                objectClusters.add(group);
            }
        }


        return objectClusters;
    }

    private static void exploreBorders(int obj, boolean[][] borderMatrix, boolean[] visited, Idea group, Idea objects) {
        visited[obj] = true;
        group.add(objects.getL().get(obj));

        for (int i = 0; i < borderMatrix[obj].length; i++) {
            if (borderMatrix[obj][i] && !visited[i]) {
                exploreBorders(i, borderMatrix, visited, group, objects);
            }
        }
    }

    public Idea getUnObjs() {
        return unObjsCF;
    }

    public Idea getIdObjsCF() {
        return idObjsCF;
    }

    public Idea getDetectedObjectsCF() {
        return idObjsCF;
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
