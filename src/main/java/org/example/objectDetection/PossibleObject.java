package org.example.objectDetection;

import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

public class PossibleObject {
    private MatOfPoint contour;
    private Rect boundRect;

    private Point centerPoint;
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
}
