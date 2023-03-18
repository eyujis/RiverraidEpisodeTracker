package org.example.mind.codelets.object_detection;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.Random;

public class IndividualObject {
    private Rect boundRect;

    private Point centerPoint;
    private Random rng = new Random();
    private Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));

    public IndividualObject(MatOfPoint contour) {

        MatOfPoint2f contourPoly = new MatOfPoint2f();
        Imgproc.approxPolyDP(new MatOfPoint2f(contour.toArray()), contourPoly, 3, true);

        boundRect = Imgproc.boundingRect(new MatOfPoint(contourPoly.toArray()));

        setCenterFromBoundRect();
    }

    public IndividualObject(Rect boundRect) {
        this.boundRect = boundRect;

        setCenterFromBoundRect();
    }

    private void setCenterFromBoundRect()   {
        Point tl = boundRect.tl();
        Point br = boundRect.br();
        double xCenter = (tl.x + br.x)/2;
        double yCenter = (tl.y + br.y)/2;

        centerPoint = new Point(xCenter, yCenter);
    }

    public Point getCenterPoint() {
        return centerPoint;
    }

    public Rect getBoundRect() {
        return boundRect;
    }

    public void setBoundRect(Rect boundRect) {
       this.boundRect = boundRect;
    }

    public Scalar getColor() {
        return color;
    }

    public void setColor(Scalar color) {
        this.color = color;
    }

}
