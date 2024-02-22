package org.example.mind.codelets.object_proposer;

import br.unicamp.cst.representation.idea.Idea;
import org.opencv.core.Mat;

public class VSSketchpad {

    public Idea getUnFragmentsFromFrame(Mat frame, Idea fragmentCategories) {
        Idea possibleFragments = new RandomSaccadesAlgorithm().getAllUnObjects(frame);
        return possibleFragments;
    }
}
