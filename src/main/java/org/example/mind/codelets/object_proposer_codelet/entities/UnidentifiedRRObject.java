package org.example.mind.codelets.object_proposer_codelet.entities;

import org.opencv.core.*;

import java.util.List;

public class UnidentifiedRRObject extends RRObject {
    public UnidentifiedRRObject(double[] colorBGR, List<MatOfPoint> contours) {
        this.color = colorBGR;
        this.contours = contours;
        this.externalContour = contours.get(0);
        this.boundRect = getBoundRectFromContour(externalContour);
        this.centerPoint = getCenterFromBoundRect(this.boundRect);
    }
}
