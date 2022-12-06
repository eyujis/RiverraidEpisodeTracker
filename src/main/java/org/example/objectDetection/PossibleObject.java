package org.example.objectDetection;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.Random;

public class PossibleObject {
    private MatOfPoint contour;
    private Rect boundRect;

    private Point centerPoint;
    private Random rng = new Random();
    private Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));

    public PossibleObject(MatOfPoint contour) {
        this.contour = contour;

        MatOfPoint2f contourPoly = new MatOfPoint2f();
        Imgproc.approxPolyDP(new MatOfPoint2f(contour.toArray()), contourPoly, 3, true);

        boundRect = Imgproc.boundingRect(new MatOfPoint(contourPoly.toArray()));

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

    public MatOfPoint getContour() {
        return contour;
    }

    public Scalar getColor() {
        return color;
    }

    public void setColor(Scalar color) {
        this.color = color;
    }
}
