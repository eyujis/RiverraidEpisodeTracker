package org.example.mind.codelets.object_proposer_codelet.entity_trackers;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_proposer_codelet.FragmentComparator;
import org.example.mind.codelets.object_proposer_codelet.entities.FragmentFactory;

public class FragmentTracker {

    private Idea fragsPF = new Idea("fragsPF", "", 0);
    private Idea unFragsCF = new Idea("unFragsCF", "", 0);
    private Idea newUnFragsCF = new Idea("newUnFragsCF", "", 0);
    private Idea idFragsCF = new Idea("idFragsCF", "", 0);

    private FragmentComparator fragComparator = new FragmentComparator();
    private FragmentFactory fragFactory = new FragmentFactory();

    private boolean firstFrame = true;

    public Idea identifyBetweenFrames(Idea detectedFrags) {
        if(firstFrame == true) {
            firstFrame = false;
            idFragsCF = fragFactory.createIdFragsFromUnFrags(detectedFrags);
            return idFragsCF;
        }

        fragsPF = new Idea("fragsPF", "", 0);
        for(Idea idFragCF : idFragsCF.getL()) {
            fragsPF.add(idFragCF);
        }

        if(newUnFragsCF.getL().size()>0) {
            for(Idea newUnFragCF : fragFactory.createIdFragsFromUnFrags(newUnFragsCF).getL()) {
                fragsPF.add(newUnFragCF);
            }
        }

        idFragsCF = new Idea("idFragsCF", "", 0);
        newUnFragsCF = new Idea("newUnFragsCF", "", 0);

        unFragsCF = detectedFrags;

        int n_fragments = Math.max(unFragsCF.getL().size(), fragsPF.getL().size());
        double[][] dataMatrix = new double[n_fragments][n_fragments];
        setToMaxValue(dataMatrix);

        // Assign fragments between frames
        for(int i = 0; i< unFragsCF.getL().size(); i++) {
            for(int j = 0; j< fragsPF.getL().size(); j++) {
                boolean closeCenterDistance = fragComparator.closeCenterDistance(unFragsCF.getL().get(i), fragsPF.getL().get(j));
                double centerDistance = fragComparator.getCenterDistance(unFragsCF.getL().get(i), fragsPF.getL().get(j));
                double hueDistance = fragComparator.getHueDistance(unFragsCF.getL().get(i), fragsPF.getL().get(j));
                boolean sameColor = fragComparator.areSameColor(unFragsCF.getL().get(i), fragsPF.getL().get(j));
                boolean similarRectShape = fragComparator.haveSimilarRectShape(unFragsCF.getL().get(i), fragsPF.getL().get(j));

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

            //remove fragments from last frame that disappeared in the current frame
            if(assignment[i][1] <= unFragsCF.getL().size()-1) {
                //new fragments in the current frame that where not present in the previous frame
                if(assignment[i][0] > fragsPF.getL().size()-1) {
                    newUnFragsCF.getL().add(unFragsCF.getL().get(assignment[i][1]));
                    //assigns fragments in the previous frame to fragments in the current frame
                } else {
                    fragFactory.transferPropertyValues(fragsPF.getL().get(assignment[i][0]), unFragsCF.getL().get(assignment[i][1]));
                    idFragsCF.getL().add(fragsPF.getL().get(assignment[i][0]));
                }
            }
        }

        return idFragsCF;
    }

    public void setToMaxValue(double[][] matrix) {
        for(int i=0; i<matrix.length; i++) {
            for(int j=0; j<matrix[0].length; j++) {
                matrix[i][j] = Double.MAX_VALUE;
            }
        }
    }

}
