package org.example.mind.codelets.object_proposer_utils;
import org.jsoar.kernel.parser.PossibleSymbolTypes;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ObjectProposer {
    static final int NO_MATCH = -1;
    static final double MIN_DISTANCE = 15;
    static final int MIN_SHAPE_DIFF = 15;
    static final double MIN_HUE_DIFF = 0.7;
    
    // PF: from previous frame
    // CF: from current frame
    private ArrayList<PotentialObject> objsPF = new ArrayList<PotentialObject>();
    private ArrayList<PotentialObject> potentialObjsCF = new ArrayList<PotentialObject>();
    private ArrayList<PotentialObject> newObjsCF = new ArrayList<PotentialObject>();
    private ArrayList<PotentialObject> confirmedObjsCF = new ArrayList<PotentialObject>();
    
    private MaskedContourDectector contourDetector = new MaskedContourDectector();

    private boolean firstFrame = true;

    public void update(Mat currentFrame) {

        if(firstFrame == true) {
            firstFrame=false;
            confirmedObjsCF = getPotentialObjectsFromFrame(currentFrame);
            return;
        }

        objsPF = new ArrayList<PotentialObject>();
        objsPF.addAll(confirmedObjsCF);
        objsPF.addAll(newObjsCF);

        confirmedObjsCF = new ArrayList<PotentialObject>();
        newObjsCF = new ArrayList<PotentialObject>();

        potentialObjsCF = getPotentialObjectsFromFrame(currentFrame);

        int[] matchIdx = new int[potentialObjsCF.size()];
        for(int k=0; k<matchIdx.length; k++) {
            matchIdx[k] = NO_MATCH;
        }

        for(int i=0; i<potentialObjsCF.size(); i++) {
            //find potential object's closest match
            matchIdx[i] = closestMatchObjectIdx(potentialObjsCF.get(i), objsPF);
        }

        for(int j=0; j<matchIdx.length; j++) {
            if(matchIdx[j] == NO_MATCH) {
                newObjsCF.add(potentialObjsCF.get(j));
            } else {
                objsPF.get(matchIdx[j]).updateObjectToCF(potentialObjsCF.get(j));
                confirmedObjsCF.add(objsPF.get(matchIdx[j]));
            }
        }

    }

    private int closestMatchObjectIdx(PotentialObject obj1,
                                     ArrayList<PotentialObject> objs) {

        double closestDistance = MIN_DISTANCE;
        int closestObjIdx = NO_MATCH;

        boolean[] matchedObjsIdx = new boolean[objs.size()];
        for(int j=0; j<matchedObjsIdx.length; j++) {
            matchedObjsIdx[j] = false;
        }

        for(int i=0; i<objs.size(); i++) {
            double centerDistance = getCenterDistance(obj1, objs.get(i));
            double hueDistance =Imgproc.matchShapes(obj1.getContour(), objs.get(i).getContour(), Imgproc.CV_CONTOURS_MATCH_I1, 0.0);

            if(true
                    && haveMinimumRectDistance(obj1, objs.get(i))
                    && haveSimilarRectShape(obj1, objs.get(i))
                    && centerDistance<MIN_DISTANCE
                    && hueDistance<MIN_HUE_DIFF
                    && matchedObjsIdx[i] == false) {

                if(centerDistance<closestDistance) {
                    closestDistance = centerDistance;
                    closestObjIdx = i;
                    matchedObjsIdx[i] = true;
                }
            }
        }
        return closestObjIdx;
    }

    private double getCenterDistance(PotentialObject obj1, PotentialObject obj2) {
        return pointDistance(obj1.getCenterPoint(), obj2.getCenterPoint());
    }

    private boolean haveMinimumRectDistance(PotentialObject obj1, PotentialObject obj2) {
        Rect rect1 = obj1.getBoundRect();
        Rect rect2 = obj2.getBoundRect();
        if(rectDistance(rect1, rect2) < MIN_DISTANCE) {return true;}
        return false;
    }

    private double rectDistance(Rect rect1, Rect rect2) {
        // based on https://stackoverflow.com/a/26178015
        double x1tl = rect1.tl().x;
        double y1tl = rect1.tl().y;
        double x1br = rect1.br().x;
        double y1br = rect1.br().y;
        double x2tl = rect2.tl().x;
        double y2tl = rect2.tl().y;
        double x2br = rect2.br().x;
        double y2br = rect2.br().y;

        boolean left = x2br < x1tl;
        boolean right = x1br < x2tl;
        boolean bottom = y2br < y1tl;
        boolean top = y1br < y2tl;

        if(top && left) {
            return pointDistance(new Point(x1tl, y1br), new Point(x2br, y2tl));
        } else if(left && bottom) {
            return pointDistance(new Point(x1tl, y1tl), new Point(x2br, y2br));
        } else if(bottom && right) {
            return pointDistance(new Point(x1br, y1tl), new Point(x2tl, y2br));
        } else if(right && top) {
            return pointDistance(new Point(x1br, y1br), new Point(x2tl, y2tl));
        } else if(left) {
            return x1tl - x2br;
        } else if(right) {
            return x2tl - x1br;
        } else if(bottom) {
            return y1tl - y2br;
        } else if(top) {
            return y2tl - y1br;
        } else {
            return 0;
        }
    }

    public double pointDistance(Point p1, Point p2) {
        double xSquared = Math.pow(p1.x-p2.x,2);
        double ySquared = Math.pow(p1.y-p2.y,2);
        return (Math.sqrt(xSquared+ySquared));
    }

    private boolean haveSimilarRectShape(PotentialObject obj1, PotentialObject obj2) {
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

    private ArrayList<PotentialObject> getPotentialObjectsFromFrame(Mat frameImage) {
        List<MatOfPoint> contours = contourDetector.getContoursFromFrame(frameImage);

        ArrayList<PotentialObject> potentialObjectList = new ArrayList<>();

        for(MatOfPoint contour: contours) {
            PotentialObject potentialObject = new PotentialObject(contour);
            potentialObjectList.add(potentialObject);
        }

        return potentialObjectList;
    }

    public ArrayList<PotentialObject> getConfirmedObjsCF() {
        return confirmedObjsCF;
    }
}
