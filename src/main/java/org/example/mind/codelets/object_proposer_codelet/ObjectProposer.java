package org.example.mind.codelets.object_proposer_codelet;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_cat_learner.entities.ObjectCategory;
import org.example.mind.codelets.object_cat_learner.entities.PObjectCategory;
import org.example.mind.codelets.object_cat_learner.entities.WObjectCategory;
import org.example.mind.codelets.object_proposer_codelet.entities.IdentifiedRRObject;
import org.example.mind.codelets.object_proposer_codelet.entities.RRObject;
import org.example.mind.codelets.object_proposer_codelet.entities.UnidentifiedRRObject;
import org.example.mind.codelets.object_proposer_codelet.object_tracker.ObjectTracker;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.util.ArrayList;

public class ObjectProposer {

    private VSSketchpad vsSketchpad;
    private ObjectTracker objTracker;
    private ObjectComparator objectComparator;

    private double MIN_CLUSTER_DISTANCE = 2;

    private ArrayList<UnidentifiedRRObject> unObjsCF = new ArrayList<UnidentifiedRRObject>();
    private ArrayList<IdentifiedRRObject> idObjsCF = new ArrayList<IdentifiedRRObject>();

    private boolean firstFrame = true;

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
        // why missile appears when this is commented?
        for(IdentifiedRRObject idObj : idObjsCF) {
            idObj.setAssignedObjCategory(null);
        }

        for(PObjectCategory objCat : objectCategories) {
            for (IdentifiedRRObject idObj : idObjsCF) {
                if (objCat.membership(idObj.getObjectIdea()) == 1) {
                    idObj.setAssignedObjCategory(objCat);
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

    public ArrayList<UnidentifiedRRObject> getUnObjs() {
        return unObjsCF;
    }

    public ArrayList<IdentifiedRRObject> getIdObjsCF() {
        return idObjsCF;
    }

    public Idea getDetectedObjectsCF() {
        Idea detectedObject = new Idea("detectedObjects", "", 0);
        for(IdentifiedRRObject idObj : idObjsCF) {
            detectedObject.add(idObj.getObjectIdea());
        }
        return detectedObject;
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
