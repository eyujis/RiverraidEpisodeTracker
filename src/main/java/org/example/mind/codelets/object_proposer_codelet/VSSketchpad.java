package org.example.mind.codelets.object_proposer_codelet;

import br.unicamp.cst.representation.idea.Idea;
import org.opencv.core.Mat;

public class VSSketchpad {
    public Idea getUnFragmentsFromFrame(Mat frame) {
        Idea unObjects;
        unObjects = new RandomSaccadesAlgorithm().getAllUnObjects(frame);
        return unObjects;
    }
}
