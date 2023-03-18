package org.example.mind.codelets.object_detection;

import org.opencv.core.Point;
import org.opencv.core.Rect;

public class ComposedObject {
    IndividualObject lastFrameIndividualObject;
    IndividualObject currentFrameIndividualObject;


    public ComposedObject(IndividualObject lastFrameIndividualObject,
                          IndividualObject currentFrameIndividualObject) {
        this.lastFrameIndividualObject = lastFrameIndividualObject;
        this.currentFrameIndividualObject = currentFrameIndividualObject;
        this.currentFrameIndividualObject.setColor(lastFrameIndividualObject.getColor());
    }

    public ComposedObject(ComposedObject composedObject1, ComposedObject composedObject2) {
        Rect lastFrame1Rect = composedObject1.getLastFrameIndividualObject().getBoundRect();
        Rect lastFrame2Rect = composedObject2.getLastFrameIndividualObject().getBoundRect();
        Rect lastFrameOutsideRect = createOutsideRect(lastFrame1Rect, lastFrame2Rect);

        Rect currentFrame1Rect = composedObject1.getCurrentFrameIndividualObject().getBoundRect();
        Rect currentFrame2Rect = composedObject2.getCurrentFrameIndividualObject().getBoundRect();
        Rect currentFrameOutsideRect = createOutsideRect(currentFrame1Rect, currentFrame2Rect);

        IndividualObject lastFrameIndividualObject = new IndividualObject(lastFrameOutsideRect);
        IndividualObject currentFrameIndividualObject = new IndividualObject(currentFrameOutsideRect);

        this.lastFrameIndividualObject = lastFrameIndividualObject;
        this.currentFrameIndividualObject = currentFrameIndividualObject;
        this.currentFrameIndividualObject.setColor(composedObject1.getCurrentFrameIndividualObject().getColor());
    }

    public IndividualObject getCurrentFrameIndividualObject() {
        return currentFrameIndividualObject;
    }

    public IndividualObject getLastFrameIndividualObject() {
        return lastFrameIndividualObject;
    }
    public Rect createOutsideRect(Rect rect1, Rect rect2) {
        int x1Start = rect1.x;
        int x1End = rect1.x + rect1.width;
        int y1Start = rect1.y;
        int y1End = rect1.y + rect1.height;

        int x2Start = rect2.x;
        int x2End = rect2.x + rect2.width;
        int y2Start = rect2.y;
        int y2End = rect2.y + rect2.height;

        int xOutsideRectStart;
        int yOutsideRectStart;
        int xOutsideRectEnd;
        int yOutsideRectEnd;


        if(x1Start < x2Start) {
            xOutsideRectStart = x1Start;
        } else {
            xOutsideRectStart = x2Start;
        }

        if(y1Start < y2Start) {
            yOutsideRectStart = y1Start;
        } else {
            yOutsideRectStart = y2Start;
        }

        if(x1End > x2End) {
            xOutsideRectEnd = x1End;
        } else {
            xOutsideRectEnd = x2End;
        }

        if(y1End > y2End) {
            yOutsideRectEnd = y1End;
        } else {
            yOutsideRectEnd = y2End;
        }

        Point tlOutsideRect = new Point(xOutsideRectStart, yOutsideRectStart);
        Point brOutsideRect = new Point(xOutsideRectEnd, yOutsideRectEnd);

        return new Rect(tlOutsideRect, brOutsideRect);
    }
}
