package org.example.mind.codelets.object_proposer_codelet.object_tracker;

import org.example.mind.codelets.object_proposer_codelet.ObjectComparator;
import org.example.mind.codelets.object_proposer_codelet.entities.IdentifiedRRObject;
import org.example.mind.codelets.object_proposer_codelet.entities.UnidentifiedRRObject;

import java.util.ArrayList;

public class ObjectTracker {

    private ArrayList<IdentifiedRRObject> objsPF = new ArrayList<IdentifiedRRObject>();
    private ArrayList<UnidentifiedRRObject> unObjsCF = new ArrayList<UnidentifiedRRObject>();
    private ArrayList<UnidentifiedRRObject> newUnObjsCF = new ArrayList<UnidentifiedRRObject>();
    private ArrayList<IdentifiedRRObject> idObjsCF = new ArrayList<IdentifiedRRObject>();

    private ObjectComparator objComparator = new ObjectComparator();

    private boolean firstFrame = true;

    public ArrayList<IdentifiedRRObject> identifyBetweenFrames(ArrayList<UnidentifiedRRObject> detectedObjs) {
        if(firstFrame == true) {
            firstFrame = false;
            idObjsCF = createIdObjsFromUnObjs(detectedObjs);
            return idObjsCF;
        }

        objsPF = new ArrayList<IdentifiedRRObject>();
        objsPF.addAll(idObjsCF);
        objsPF.addAll(createIdObjsFromUnObjs(newUnObjsCF));

        idObjsCF = new ArrayList<IdentifiedRRObject>();
        newUnObjsCF = new ArrayList<UnidentifiedRRObject>();

        unObjsCF = detectedObjs;

        int n_objects = Math.max(unObjsCF.size(), objsPF.size());
        double[][] dataMatrix = new double[n_objects][n_objects];
        setToMaxValue(dataMatrix);

        // Assign objects between frames
        for(int i=0; i<unObjsCF.size(); i++) {
            for(int j=0; j<objsPF.size(); j++) {
                boolean closeCenterDistance = objComparator.closeCenterDistance(unObjsCF.get(i).getObjectIdea(), objsPF.get(j).getObjectIdea());
                double centerDistance = objComparator.getCenterDistance(unObjsCF.get(i).getObjectIdea(), objsPF.get(j).getObjectIdea());
                double hueDistance = objComparator.getHueDistance(unObjsCF.get(i).getObjectIdea(), objsPF.get(j).getObjectIdea());
                boolean sameColor = objComparator.areSameColor(unObjsCF.get(i).getObjectIdea(), objsPF.get(j).getObjectIdea());
                boolean similarRectShape = objComparator.haveSimilarRectShape(unObjsCF.get(i).getObjectIdea(), objsPF.get(j).getObjectIdea());

                if(closeCenterDistance
//                        && hueDistance<=MIN_HUE_DIFF
                        && similarRectShape
                        && sameColor) {
                    dataMatrix[i][j] = centerDistance/100 + hueDistance;
                }
            }
        }

        HungarianAlgorithm ha = new HungarianAlgorithm(dataMatrix);
        int[][] assignment = ha.findOptimalAssignment();

        for(int i=0; i<assignment.length; i++) {

            //remove objects from last frame that disappeared in the current frame
            if(assignment[i][1] <= unObjsCF.size()-1) {
                //new objects in the current frame that where not present in the previous frame
                if(assignment[i][0] > objsPF.size()-1) {
                    newUnObjsCF.add(unObjsCF.get(assignment[i][1]));
                    //assigns objects in the previous frame to objects in the current frame
                } else {
                    objsPF.get(assignment[i][0]).updateProperties(unObjsCF.get(assignment[i][1]));
                    idObjsCF.add(objsPF.get(assignment[i][0]));
                }
            }
        }

        return idObjsCF;
    }

    public void setToMaxValue(double[][] matrix) {
        for(int i=0; i<matrix.length; i++) {
            for(int j=0; j<matrix[0].length; j++) {
                matrix[i][j] = Double.MAX_VALUE;
            }
        }
    }

    public ArrayList<IdentifiedRRObject> createIdObjsFromUnObjs(ArrayList<UnidentifiedRRObject> unObjs) {
        ArrayList<IdentifiedRRObject> idObjs = new ArrayList<IdentifiedRRObject>();
        for(UnidentifiedRRObject unObj: unObjs) {
            idObjs.add(new IdentifiedRRObject(unObj));
        }
        return idObjs;
    }

}
