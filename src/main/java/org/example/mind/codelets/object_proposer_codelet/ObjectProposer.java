package org.example.mind.codelets.object_proposer_codelet;

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

    private double MIN_CLUSTER_DISTANCE = 0;

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
                if (objCat.membership(idObj) == 1) {
                    idObj.setAssignedObjCategory(objCat);
                }
            }
        }
    }

    public void assignWCategories(ArrayList<WObjectCategory> objectCategories) {
//         why missile appears when this is commented?
//        for(IdentifiedRRObject idObj : idObjsCF) {
//            idObj.getAssignedCategory().setColorIdScalar(new Scalar(255,255,255));
//        }

        ArrayList<ArrayList<IdentifiedRRObject>> objectClusters = extractObjectClusters(idObjsCF);
        for(ArrayList<IdentifiedRRObject> objectCluster : objectClusters) {
//            System.out.println(objectCategories.size());
            for(WObjectCategory objectCategory : objectCategories) {
                if(objectCategory.membership(objectCluster) == 1) {
                    for(IdentifiedRRObject obj: objectCluster) {
                        obj.getAssignedCategory().setColorIdScalar(objectCategory.getColorIdScalar());
//                        System.out.println("AAAAAAAAAAAA");
                    }
                }
            }
        }


    }

    public ArrayList<ArrayList<IdentifiedRRObject>> extractObjectClusters(ArrayList<IdentifiedRRObject> objectInstances) {

        boolean[][] borderMatrix = initializeBooleanMatrix(objectInstances.size());

        for(int i=0; i<objectInstances.size(); i++) {
            for(int j=0; j<objectInstances.size(); j++) {
                RRObject obj1 = objectInstances.get(i);
                RRObject obj2 = objectInstances.get(j);

                if(i!=j && objectComparator.rectDistance(obj1, obj2)<=MIN_CLUSTER_DISTANCE) {
                    borderMatrix[i][j] = true;
                }
            }
        }

        boolean[] visited = new boolean[objectInstances.size()];
        for (int i = 0; i < visited.length; i++) {
            visited[i] = false;
        }

        ArrayList<ArrayList<IdentifiedRRObject>> objectClusters = new ArrayList<>();

        for (int i = 0; i < objectInstances.size(); i++) {
            if (!visited[i]) {
                ArrayList<IdentifiedRRObject> group = new ArrayList<>();
                exploreBorders(i, borderMatrix, visited, group, objectInstances);
                objectClusters.add(group);
            }
        }


        return objectClusters;
    }

    private static void exploreBorders(int obj, boolean[][] borderMatrix, boolean[] visited, ArrayList<IdentifiedRRObject> group, ArrayList<IdentifiedRRObject> objects) {
        visited[obj] = true;
        group.add(objects.get(obj));

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
