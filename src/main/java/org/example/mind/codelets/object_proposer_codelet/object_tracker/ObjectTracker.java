package org.example.mind.codelets.object_proposer_codelet.object_tracker;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_proposer_codelet.ObjectComparator;
import org.example.mind.codelets.object_proposer_codelet.entities.ObjectFactory;

public class ObjectTracker {

    private Idea objsPF = new Idea("objsPF", "", 0);
    private Idea unObjsCF = new Idea("unObjects", "", 0);
    private Idea newUnObjsCF = new Idea("newUnObjsCF", "", 0);
    private Idea idObjsCF = new Idea("idObjsCF", "", 0);

    private ObjectComparator objComparator = new ObjectComparator();
    private ObjectFactory objFactory = new ObjectFactory();

    private boolean firstFrame = true;

    public Idea identifyBetweenFrames(Idea detectedObjs) {
        if(firstFrame == true) {
            firstFrame = false;
            idObjsCF = objFactory.createIdObjsFromUnObjs(detectedObjs);
            return idObjsCF;
        }

        objsPF = new Idea("objsPF", "", 0);
        for(Idea idObjCF : idObjsCF.getL()) {
            objsPF.add(idObjCF);
        }

        if(newUnObjsCF.getL().size()>0) {
            for(Idea newUnObjCF : objFactory.createIdObjsFromUnObjs(newUnObjsCF).getL()) {
                objsPF.add(newUnObjCF);
            }
        }

        idObjsCF = new Idea("idObjsCF", "", 0);
        newUnObjsCF = new Idea("newUnObjsCF", "", 0);

        unObjsCF = detectedObjs;

        int n_objects = Math.max(unObjsCF.getL().size(), objsPF.getL().size());
        double[][] dataMatrix = new double[n_objects][n_objects];
        setToMaxValue(dataMatrix);

        // Assign objects between frames
        for(int i=0; i<unObjsCF.getL().size(); i++) {
            for(int j=0; j<objsPF.getL().size(); j++) {
                boolean closeCenterDistance = objComparator.closeCenterDistance(unObjsCF.getL().get(i), objsPF.getL().get(j));
                double centerDistance = objComparator.getCenterDistance(unObjsCF.getL().get(i), objsPF.getL().get(j));
                double hueDistance = objComparator.getHueDistance(unObjsCF.getL().get(i), objsPF.getL().get(j));
                boolean sameColor = objComparator.areSameColor(unObjsCF.getL().get(i), objsPF.getL().get(j));
                boolean similarRectShape = objComparator.haveSimilarRectShape(unObjsCF.getL().get(i), objsPF.getL().get(j));

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
            if(assignment[i][1] <= unObjsCF.getL().size()-1) {
                //new objects in the current frame that where not present in the previous frame
                if(assignment[i][0] > objsPF.getL().size()-1) {
                    newUnObjsCF.getL().add(unObjsCF.getL().get(assignment[i][1]));
                    //assigns objects in the previous frame to objects in the current frame
                } else {
                    objFactory.transferPropertyValues(objsPF.getL().get(assignment[i][0]), unObjsCF.getL().get(assignment[i][1]));
//                    objsPF.getL().get(assignment[i][0]).updateProperties(unObjsCF.getL().get(assignment[i][1]));
                    idObjsCF.getL().add(objsPF.getL().get(assignment[i][0]));
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

}
