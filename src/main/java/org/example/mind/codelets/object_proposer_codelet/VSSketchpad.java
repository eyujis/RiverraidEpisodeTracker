package org.example.mind.codelets.object_proposer_codelet;

import org.example.mind.codelets.object_proposer_codelet.RandomSaccadesAlgorithm;
import org.example.mind.codelets.object_proposer_codelet.entities.UnidentifiedRRObject;
import org.opencv.core.Mat;

import java.util.ArrayList;

public class VSSketchpad {
    public ArrayList<UnidentifiedRRObject> getUnObjectsFromFrame(Mat frame) {
        ArrayList<UnidentifiedRRObject> unObjects;
        unObjects = new RandomSaccadesAlgorithm().getAllUnObjects(frame);
        return unObjects;
    }
}
