package org.example.mind.codelets.object_proposer_codelet.entities;

import org.example.mind.codelets.object_cat_learner.entities.ObjectCategory;
import org.example.mind.codelets.object_cat_learner.entities.PObjectCategory;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.List;

//River Raid Object
public class RRObject {
    double[] color;
    List<MatOfPoint> contours;
    MatOfPoint externalContour;
    Rect boundRect;
    Point centerPoint;

    private PObjectCategory assignedObjCategory = null;

    Rect getBoundRectFromContour(MatOfPoint externalContour) {
        MatOfPoint2f contourPoly = new MatOfPoint2f();
        Imgproc.approxPolyDP(new MatOfPoint2f(externalContour.toArray()), contourPoly, 3, true);
        Rect boundRect = Imgproc.boundingRect(new MatOfPoint(contourPoly.toArray()));
        return boundRect;
    }

    Point getCenterFromBoundRect(Rect rect)   {
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

    public PObjectCategory getAssignedCategory() {
        return assignedObjCategory;
    }

    public void setAssignedObjCategory(PObjectCategory assignedObjCategory) {
        this.assignedObjCategory = assignedObjCategory;
    }
}
