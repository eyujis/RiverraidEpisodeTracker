package org.example.mind.codelets.object_proposer_utils;

import org.opencv.core.*;
import java.util.ArrayList;
import java.util.List;

public class ObjectListProposer {
    private MaskedContourDectector contourDetector = new MaskedContourDectector();
    private List<ComposedObject> composedObjectListFromCurrentFrame = new ArrayList<>();
    private List<ComposedObject> composedObjectMergedListFromCurrentFrame;
    private List<IndividualObject> individualObjectListFromCurrentFrame;
    private List<IndividualObject> individualObjectListFromLastFrame;
    private boolean firstFrame = true;

    public ObjectListProposer() {

    }

    public void update(Mat frameImage) {
        if(firstFrame == true) {
            firstFrame=false;
            individualObjectListFromCurrentFrame = getIndividualObjectListFromFrame(frameImage);
            return;
        }
        composedObjectListFromCurrentFrame = new ArrayList<>();
        individualObjectListFromLastFrame = individualObjectListFromCurrentFrame;
        individualObjectListFromCurrentFrame = getIndividualObjectListFromFrame(frameImage);

        for(int i = 0; i< individualObjectListFromCurrentFrame.size(); i++) {
            IndividualObject closestLastObject = null;
//            double closestObjectDistance = 15;
            double closestObjectDistance = 50;

            IndividualObject currentObject = individualObjectListFromCurrentFrame.get(i);
            Point currentObjectCenter = currentObject.getCenterPoint();

            boolean[] matchedObjectsFromLastFrame = new boolean[individualObjectListFromLastFrame.size()];
            for(int k=0; k<matchedObjectsFromLastFrame.length; k++) {
                matchedObjectsFromLastFrame[k] = false;
            }
            int closestObjectFromLastFrameIdx = -1;

            for(int j = 0; j< individualObjectListFromLastFrame.size(); j++)  {
                IndividualObject lastObject = individualObjectListFromLastFrame.get(j);
                Point lastObjectCenter = lastObject.getCenterPoint();
                double centerDistance = pointDistance(currentObjectCenter, lastObjectCenter);

//                if(centerDistance < 15
                if(centerDistance < 100
                        && centerDistance < closestObjectDistance
                        && hasSimilarRectDimension(currentObject, lastObject)
                ) {
                    closestLastObject = lastObject;
                    closestObjectDistance = centerDistance;
                    closestObjectFromLastFrameIdx = j;
                }
            }

            if(closestLastObject!=null &&
                    matchedObjectsFromLastFrame[closestObjectFromLastFrameIdx] == false
            ) {
                matchedObjectsFromLastFrame[closestObjectFromLastFrameIdx]= true;
                ComposedObject currentComposedObject = new ComposedObject(closestLastObject, currentObject);
//                currentObject.setColor(closestLastObject.getColor());
                composedObjectListFromCurrentFrame.add(currentComposedObject);
            }
        }

        boolean[] idxItemUsed = new boolean[composedObjectListFromCurrentFrame.size()];
        for(int i=0; i<idxItemUsed.length; i++) {
            idxItemUsed[i] = false;
        }

        composedObjectMergedListFromCurrentFrame = new ArrayList<ComposedObject>();
        for(int i = 0; i< composedObjectListFromCurrentFrame.size(); i++)  {
            ComposedObject composedObject = new ComposedObject(composedObjectListFromCurrentFrame.get(i).getLastFrameIndividualObject(),
                    composedObjectListFromCurrentFrame.get(i).getCurrentFrameIndividualObject());
            if(idxItemUsed[i] == false) {
                idxItemUsed[i] = true;

                for(int j = 0; j< composedObjectListFromCurrentFrame.size(); j++) {
                    ComposedObject composedObject2 = new ComposedObject(composedObjectListFromCurrentFrame.get(j).getLastFrameIndividualObject(),
                            composedObjectListFromCurrentFrame.get(j).getCurrentFrameIndividualObject());
                    if(idxItemUsed[j]==false
                    && objectsAreClose(composedObject.getLastFrameIndividualObject(), composedObject2.getLastFrameIndividualObject())
                    && objectsAreClose(composedObject.getCurrentFrameIndividualObject(), composedObject2.getCurrentFrameIndividualObject())) {
                        idxItemUsed[j] = true;
                        composedObject = new ComposedObject(composedObject, composedObject2);
                    }
                }
                composedObjectMergedListFromCurrentFrame.add(composedObject);
            }
        }
    }

    public boolean objectsAreClose(IndividualObject individualObject1, IndividualObject individualObject2) {
        Rect rectO1 = individualObject1.getBoundRect();
        Rect rectO2 = individualObject2.getBoundRect();

        if(rectDistance(rectO1, rectO2)<3) {
            return true;
        }
        return false;
    }

    public List<ComposedObject> getComposedObjectListFromCurrentFrame() {
        return composedObjectListFromCurrentFrame;
    }

    public List<ComposedObject> getComposedObjectMergedListFromCurrentFrame() {
        return composedObjectMergedListFromCurrentFrame;
    }
    public double pointDistance(Point p1, Point p2) {
        double xSquared = Math.pow(p1.x-p2.x,2);
        double ySquared = Math.pow(p1.y-p2.y,2);
        return (Math.sqrt(xSquared+ySquared));
    }

    public double rectDistance(Rect rect1, Rect rect2) {
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

    public boolean hasSimilarRectDimension(IndividualObject object1, IndividualObject object2) {
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

    public List<IndividualObject> getIndividualObjectListFromFrame(Mat frameImage) {
       List<MatOfPoint> contours = contourDetector.getContoursFromFrame(frameImage);

       List<IndividualObject> individualObjectList = new ArrayList<>();

        for(int i=0; i<contours.size(); i++) {
            IndividualObject individualObject = new IndividualObject(contours.get(i));
            individualObjectList.add(individualObject);
        }

        return individualObjectList;
    }

}
