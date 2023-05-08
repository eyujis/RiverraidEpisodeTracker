package org.example.mind.codelets.object_proposer_utils;

import org.example.mind.codelets.object_proposer_utils.entities.UnidentifiedObject;
import org.opencv.core.Mat;

import java.util.ArrayList;

public class VSSketchpad {
    public ArrayList<UnidentifiedObject> getUnObjectsFromFrame(Mat frame) {
        ArrayList<UnidentifiedObject> unObjects;
        unObjects = new RandomSaccadesAlgorithm().getAllUnObjects(frame);
        return unObjects;
    }
}
