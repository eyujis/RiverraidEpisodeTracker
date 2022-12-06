package org.example.objectDetection;

import org.opencv.core.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ObjectListProposer {
    private ContourDetector contourDetector = new ContourDetector();
    private List<ExistentObject> existentObjectListFromCurrentFrame = new ArrayList<>();
    private List<ExistentObject> existentObjectConcatListFromCurrentFrame = new ArrayList<>();
    private List<PossibleObject> possibleObjectListFromCurrentFrame;
    private List<PossibleObject> possibleObjectListFromLastFrame;
    private boolean firstUpdate = true;

    public ObjectListProposer() {

    }

    public void update(Mat frameImage) {
        if(firstUpdate == true) {
            firstUpdate=false;
            possibleObjectListFromCurrentFrame = getPossibleObjectListFromFrame(frameImage);
            return;
        }
        existentObjectListFromCurrentFrame = new ArrayList<>();
        possibleObjectListFromLastFrame = possibleObjectListFromCurrentFrame;
        possibleObjectListFromCurrentFrame = getPossibleObjectListFromFrame(frameImage);

        for(int i=0; i<possibleObjectListFromCurrentFrame.size(); i++) {
            PossibleObject closestLastObject = null;
            double closestObjectDistance = 15;

            PossibleObject currentObject = possibleObjectListFromCurrentFrame.get(i);
            Point currentObjectCenter = currentObject.getCenterPoint();


            for(int j=0; j<possibleObjectListFromLastFrame.size(); j++)  {
                PossibleObject lastObject = possibleObjectListFromLastFrame.get(j);
                Point lastObjectCenter = lastObject.getCenterPoint();

                double centerDistance = getCenterDistance(currentObjectCenter, lastObjectCenter);

                if(centerDistance < 15
                        && centerDistance < closestObjectDistance
                        && hasSimilarRectDimension(currentObject, lastObject)) {
                    closestLastObject = lastObject;
                    closestObjectDistance = centerDistance;
                }
            }

            if(closestLastObject!=null) {
                ExistentObject currentExistentObject = new ExistentObject(closestLastObject, currentObject);
//                currentObject.setColor(closestLastObject.getColor());
                existentObjectListFromCurrentFrame.add(currentExistentObject);
            }
        }

//        boolean[] idxItemUsed = new boolean[existentObjectListFromCurrentFrame.size()];
//        for(int i=0; i<idxItemUsed.length; i++) {
//            idxItemUsed[i] = true;
//        }
//
//        existentObjectConcatListFromCurrentFrame = new ArrayList<>();
//        for(int i=0; i<existentObjectListFromCurrentFrame.size(); i++)  {
//            idxItemUsed[i] = false;
//            ExistentObject existentObject = existentObjectConcatListFromCurrentFrame.get(i);
//            for(int j=0; j<existentObjectListFromCurrentFrame.size(); j++)  {
//
//            }
//        }
    }

    public List<ExistentObject> getExistentObjectListFromCurrentFrame() {
        return existentObjectListFromCurrentFrame;
    }

    public double getCenterDistance(Point p1, Point p2) {
        double xSquared = Math.pow(p1.x-p2.x,2);
        double ySquared = Math.pow(p1.y-p2.y,2);
        return (Math.sqrt(xSquared+ySquared));
    }

    public boolean hasSimilarRectDimension(PossibleObject object1, PossibleObject object2) {
        double object1Height = object1.getBoundRect().height;
        double object2Height = object2.getBoundRect().height;

        double object1Width = object1.getBoundRect().width;
        double object2Width = object2.getBoundRect().width;

        if(hasSimilarSize(object1Height, object2Height)
                && hasSimilarSize(object1Width, object2Width)) {
            return true;
        }
        return false;
    }

    public boolean hasSimilarSize(double size1, double size2) {
        double diff = Math.abs(size1-size2);
        if(diff<5) {
            return true;
        }
        return false;
    }

    public List<PossibleObject> getPossibleObjectListFromFrame(Mat frameImage) {
       List<MatOfPoint> contours = contourDetector.getContoursFromFrame(frameImage);

       List<PossibleObject> possibleObjectList = new ArrayList<>();

        for(int i=0; i<contours.size(); i++) {
            PossibleObject possibleObject = new PossibleObject(contours.get(i));
            possibleObjectList.add(possibleObject);
        }

        return possibleObjectList;
    }

}
