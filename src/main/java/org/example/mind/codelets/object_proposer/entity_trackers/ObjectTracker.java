package org.example.mind.codelets.object_proposer.entity_trackers;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_proposer.ObjectComparator;
import org.example.mind.codelets.object_proposer.entities.ObjectFactory;

public class ObjectTracker {

    private Idea objsPF = new Idea("objsPF", "", 0);
    private Idea unObjsCF = new Idea("unObjsCF", "", 0);
    private Idea newUnObjsCF = new Idea("newUnObjsCF", "", 0);
    private Idea idObjsCF = new Idea("idObjsCF", "", 0);

    private ObjectComparator objectComparator = new ObjectComparator();
    private ObjectFactory objectFactory = new ObjectFactory();

    private boolean firstFrame = true;

    public Idea identifyBetweenFrames(Idea detectedObjs) {

        if(firstFrame == true) {
            firstFrame = false;
            idObjsCF = objectFactory.createIdObjsFromUnObjs(detectedObjs);
            return idObjsCF;
        }

        objsPF = new Idea("objsPF", "", 0);
        for(Idea idObjCF : idObjsCF.getL()) {
            objsPF.add(idObjCF);
        }

        idObjsCF = new Idea("idObjsCF", "", 0);
        newUnObjsCF = new Idea("newUnObjsCF", "", 0);

        unObjsCF = detectedObjs;

        int n_objects = Math.max(unObjsCF.getL().size(), objsPF.getL().size());
        double[][] dataMatrix = new double[n_objects][n_objects];
        setToMaxValue(dataMatrix);

        // Assign fragments between frames
        for(int i = 0; i< unObjsCF.getL().size(); i++) {
            for(int j = 0; j< objsPF.getL().size(); j++) {
                boolean closeCenterDistance = objectComparator.closeCenterDistance(unObjsCF.getL().get(i), objsPF.getL().get(j));
                double centerDistance = objectComparator.getCenterDistance(unObjsCF.getL().get(i), objsPF.getL().get(j));
                boolean similarRectShape = objectComparator.haveSimilarRectShape(unObjsCF.getL().get(i), objsPF.getL().get(j));

                if(closeCenterDistance
                        && similarRectShape) {
                    dataMatrix[i][j] = centerDistance;
                }
            }
        }

        HungarianAlgorithm ha = new HungarianAlgorithm(dataMatrix);
        int[][] assignment = ha.findOptimalAssignment();

        for(int i=0; i<assignment.length; i++) {

            //remove fragments from last frame that disappeared in the current frame
            if(assignment[i][1] <= unObjsCF.getL().size()-1) {
                //new fragments in the current frame that where not present in the previous frame
                // TODO there is an error here, sometimes the algorithm is assigning objects which are not close from
                // each other; I fixed using the minimum distance. However there is other ways to fix it in a more elegant
                // manner. Or creating a minimum criteria in the object comparator, or changing the Hungarian Algorithm;
                if(assignment[i][0] > objsPF.getL().size()-1 || !objectComparator.closeCenterDistance(objsPF.getL().get(assignment[i][0]), unObjsCF.getL().get(assignment[i][1]))
//                || !objectComparator.haveSimilarRectShape(objsPF.getL().get(assignment[i][0]), unObjsCF.getL().get(assignment[i][1]))
                ) {
                    newUnObjsCF.getL().add(unObjsCF.getL().get(assignment[i][1]));
                    //assigns fragments in the previous frame to fragments in the current frame
                } else {
                    objectFactory.transferPropertyValues(objsPF.getL().get(assignment[i][0]), unObjsCF.getL().get(assignment[i][1]));
                    idObjsCF.getL().add(objsPF.getL().get(assignment[i][0]));
                }
            }
        }

        if(newUnObjsCF.getL().size()>0) {
            for(Idea newUnObjCF : objectFactory.createIdObjsFromUnObjs(newUnObjsCF).getL()) {
                idObjsCF.add(newUnObjCF);
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
