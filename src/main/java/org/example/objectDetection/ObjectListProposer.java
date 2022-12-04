package org.example.objectDetection;

import org.opencv.core.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ObjectListProposer {
    private ContourDetector contourDetector = new ContourDetector();
    private List<PossibleObject> existentObjectListFromLastFrame;
    private List<PossibleObject> possibleObjectListFromCurrentFrame;
    private List<PossibleObject> possibleObjectListFromLastFrame;
    private boolean firstUpdate = true;

    public void update(Mat frameImage) {
        if(firstUpdate == true) {
            possibleObjectListFromCurrentFrame = getPossibleObjectListFromFrame(frameImage);
            return;
        }
        possibleObjectListFromLastFrame = possibleObjectListFromCurrentFrame;
        possibleObjectListFromCurrentFrame = getPossibleObjectListFromFrame(frameImage);
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
