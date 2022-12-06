package org.example.objectDetection;

import org.opencv.core.Scalar;

public class ExistentObject {
    PossibleObject lastFramePossibleObject;
    PossibleObject currentFramePossibleObject;


    public ExistentObject(PossibleObject lastFramePossibleObject,
                          PossibleObject currentFramePossibleObject) {
        this.lastFramePossibleObject = lastFramePossibleObject;
        this.currentFramePossibleObject = currentFramePossibleObject;
        this.currentFramePossibleObject.setColor(lastFramePossibleObject.getColor());
    }

    public PossibleObject getCurrentFramePossibleObject() {
        return currentFramePossibleObject;
    }

}
