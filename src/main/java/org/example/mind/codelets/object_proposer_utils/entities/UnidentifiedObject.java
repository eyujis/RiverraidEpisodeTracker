package org.example.mind.codelets.object_proposer_utils.entities;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class UnidentifiedObject {
    //color in BGR
    private double[] color;
    private List<MatOfPoint> contours;
    private MatOfPoint externalContour;
    private Rect boundRect;
    private Point centerPoint;

    public UnidentifiedObject(double[] colorBGR, List<MatOfPoint> contours) {
        this.color = colorBGR;
        this.contours = contours;
        this.externalContour = contours.get(0);
        this.boundRect = getBoundRectFromContour(externalContour);
        this.centerPoint = getCenterFromBoundRect(this.boundRect);
    }

    private Rect getBoundRectFromContour(MatOfPoint externalContour) {
        MatOfPoint2f contourPoly = new MatOfPoint2f();
        Imgproc.approxPolyDP(new MatOfPoint2f(externalContour.toArray()), contourPoly, 3, true);
        Rect boundRect = Imgproc.boundingRect(new MatOfPoint(contourPoly.toArray()));
        return boundRect;
    }

    private Point getCenterFromBoundRect(Rect rect)   {
        Point tl = rect.tl();
        Point br = rect.br();
        double xCenter = (tl.x + br.x)/2;
        double yCenter = (tl.y + br.y)/2;

        Point center = new Point(xCenter, yCenter);
        return center;
    }

    public double[] getColor() {
        return color;
    }

    public Scalar getScalarColor() {
        return new Scalar(color[0], color[1], color[2]);
    }

    public List<MatOfPoint> getContours() {
        return contours;
    }

    public MatOfPoint getExternalContour() {
        return externalContour;
    }

    public Rect getBoundRect() {
        return boundRect;
    }

    public Point getCenterPoint() {
        return centerPoint;
    }
}
