package org.example.mind.codelets.object_proposer_utils;

import org.example.drafts.object_proposer_v2.PotentialObject;
import org.example.mind.codelets.object_proposer_utils.entities.IdentifiedObject;
import org.example.mind.codelets.object_proposer_utils.entities.UnidentifiedObject;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ObjectProposer {
    static final int NO_MATCH = -1;
    static final double MIN_DISTANCE = 50;
    static final int MIN_SHAPE_DIFF = 50;
    static final double MIN_HUE_DIFF = 1.2;

    private VSSketchpad vsSketchpad;
    private ArrayList<IdentifiedObject> objsPF = new ArrayList<IdentifiedObject>();
    private ArrayList<UnidentifiedObject> unObjsCF = new ArrayList<UnidentifiedObject>();
    private ArrayList<UnidentifiedObject> newUnObjsCF = new ArrayList<UnidentifiedObject>();
    private ArrayList<IdentifiedObject> idObjsCF = new ArrayList<IdentifiedObject>();
    private boolean firstFrame = true;

    public ObjectProposer() {
        vsSketchpad = new VSSketchpad();
    }

    public void update(Mat frame) {

        if(firstFrame == true) {
            firstFrame = false;
            idObjsCF = createIdObjsFromUnObjs(vsSketchpad.getUnObjectsFromFrame(frame));
            return;
        }

        objsPF = new ArrayList<IdentifiedObject>();
        objsPF.addAll(idObjsCF);
        objsPF.addAll(createIdObjsFromUnObjs(newUnObjsCF));

        idObjsCF = new ArrayList<IdentifiedObject>();
        newUnObjsCF = new ArrayList<UnidentifiedObject>();

        unObjsCF = vsSketchpad.getUnObjectsFromFrame(frame);

        int n_objects = Math.max(unObjsCF.size(), objsPF.size());
        double[][] dataMatrix = new double[n_objects][n_objects];
        setToMaxValue(dataMatrix);

        for(int i=0; i<unObjsCF.size(); i++) {
            for(int j=0; j<objsPF.size(); j++) {
                double centerDistance = getCenterDistance(unObjsCF.get(i), objsPF.get(j));
                double hueDistance = Imgproc.matchShapes(unObjsCF.get(i).getExternalContour(), objsPF.get(j).getExternalContour(), Imgproc.CV_CONTOURS_MATCH_I1, 0.0);
                boolean sameColor = isSameColor(unObjsCF.get(i).getColor(), objsPF.get(j).getColor());
                if(centerDistance<=MIN_DISTANCE
                        && hueDistance<=MIN_HUE_DIFF
                        && sameColor) {
                    dataMatrix[i][j] = centerDistance;
                }
//                dataMatrix[i][j] = centerDistance;
            }
        }


        HungarianAlgorithm ha = new HungarianAlgorithm(dataMatrix);
        int[][] assignment = ha.findOptimalAssignment();

        for(int i=0; i<assignment.length; i++) {
            if(assignment[i][1] <= unObjsCF.size()-1) {
                if(assignment[i][0] > objsPF.size()-1) {
                    newUnObjsCF.add(unObjsCF.get(assignment[i][1]));
                    System.out.println(newUnObjsCF.size());
                } else {
                    objsPF.get(assignment[i][0]).updateProperties(unObjsCF.get(assignment[i][1]));
                    idObjsCF.add(objsPF.get(assignment[i][0]));
                }
            }
        }

//        int[] matchIdx = new int[unObjsCF.size()];
//        for(int k=0; k<matchIdx.length; k++) {
//            matchIdx[k] = NO_MATCH;
//        }
//
//        for(int i=0; i<unObjsCF.size(); i++) {
//            //find potential object's closest match
//            matchIdx[i] = closestMatchObjectIdx(unObjsCF.get(i), objsPF);
//        }
//
//        for(int j=0; j<matchIdx.length; j++) {
//            if(matchIdx[j] == NO_MATCH) {
//                newUnObjsCF.add(unObjsCF.get(j));
//            } else {
//                objsPF.get(matchIdx[j]).updateProperties(unObjsCF.get(j));
//                idObjsCF.add(objsPF.get(matchIdx[j]));
//            }
//        }
    }

    public void setToMaxValue(double[][] matrix) {
        for(int i=0; i<matrix.length; i++) {
            for(int j=0; j<matrix[0].length; j++) {
                matrix[i][j] = Double.MAX_VALUE;
            }
        }
    }

    private int closestMatchObjectIdx(UnidentifiedObject obj1,
                                      ArrayList<IdentifiedObject> objs) {

        double closestDistance = MIN_DISTANCE;
        int closestObjIdx = NO_MATCH;

        for(int i=0; i<objs.size(); i++) {
            double centerDistance = getCenterDistance(obj1, objs.get(i));
            double hueDistance = Imgproc.matchShapes(obj1.getExternalContour(), objs.get(i).getExternalContour(), Imgproc.CV_CONTOURS_MATCH_I1, 0.0);
            boolean sameColor = isSameColor(obj1.getColor(), objs.get(i).getColor());

            if(true
//                    && haveSimilarRectShape(obj1, objs.get(i))
                    && centerDistance<=MIN_DISTANCE
//                    && hueDistance<=MIN_HUE_DIFF
                    && sameColor
            ) {
                if(centerDistance<closestDistance) {
                    closestDistance = centerDistance;
                    closestObjIdx = i;
                }
            }
        }
        return closestObjIdx;
    }

    public ArrayList<IdentifiedObject> createIdObjsFromUnObjs(ArrayList<UnidentifiedObject> unObjs) {
        ArrayList<IdentifiedObject> idObjs = new ArrayList<IdentifiedObject>();
        for(UnidentifiedObject unObj: unObjs) {
            idObjs.add(new IdentifiedObject(unObj));
        }
        return idObjs;
    }

    private double getCenterDistance(UnidentifiedObject obj1, IdentifiedObject obj2) {
        return pointDistance(obj1.getCenterPoint(), obj2.getCenterPoint());
    }

    public double pointDistance(Point p1, Point p2) {
        double xSquared = Math.pow(p1.x-p2.x,2);
        double ySquared = Math.pow(p1.y-p2.y,2);
        return (Math.sqrt(xSquared+ySquared));
    }

    private boolean haveSimilarRectShape(UnidentifiedObject obj1, IdentifiedObject obj2) {
        double obj1Height = obj1.getBoundRect().height;
        double obj2Height = obj2.getBoundRect().height;

        double obj1Width = obj1.getBoundRect().width;
        double obj2Width = obj2.getBoundRect().width;

        if(hasSimilarLength(obj1Height, obj2Height)
                && hasSimilarLength(obj1Width, obj2Width)) {
            return true;
        }
        return false;
    }

    private boolean hasSimilarLength(double len1, double len2) {
        double lenDiff = Math.abs(len1-len2);
        if(lenDiff<MIN_SHAPE_DIFF) {return true;}
        return false;
    }

    public boolean isSameColor(double[] color1, double[] color2) {
        if(color1[0]==color2[0]
        && color1[1]==color2[1]
        && color1[2]==color2[2]) {
            return true;
        }
        return false;
    }

    public ArrayList<UnidentifiedObject> getUnObjs() {
        return unObjsCF;
    }

    public ArrayList<IdentifiedObject> getIdObjsCF() {
        return idObjsCF;
    }
}
