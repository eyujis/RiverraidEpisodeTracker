package org.example.mind.codelets.object_proposer_codelet.entities;

import br.unicamp.cst.representation.idea.Idea;
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

    public Idea getObjectIdea() {
        Idea objectIdea = new Idea("object","",0);

        // color
        Idea colorIdea = new Idea("color", "", 0);
        colorIdea.add(new Idea("R", color[2]));
        colorIdea.add(new Idea("B", color[1]));
        colorIdea.add(new Idea("G", color[0]));
        objectIdea.add(colorIdea);

        // center
        Idea centerIdea = new Idea("center", "", 0);
        centerIdea.add(new Idea("x", centerPoint.x));
        centerIdea.add(new Idea("y", centerPoint.y));
        objectIdea.add(centerIdea);

        // bounding box
        Idea boundRectIdea = new Idea("boundRect", "", 0);
        boundRectIdea.add(new Idea("height", boundRect.height));
        boundRectIdea.add(new Idea("width", boundRect.width));

        Idea tlIdea = new Idea("tl", "", 0);
        tlIdea.add(new Idea("x", boundRect.tl().x));
        tlIdea.add(new Idea("y", boundRect.tl().y));
        boundRectIdea.add(tlIdea);

        Idea brIdea = new Idea("br", "", 0);
        brIdea.add(new Idea("x", boundRect.br().x));
        brIdea.add(new Idea("y", boundRect.br().y));
        boundRectIdea.add(brIdea);

        objectIdea.add(boundRectIdea);

        // category
        Idea catIdea = new Idea("category", assignedObjCategory);
        objectIdea.add(catIdea);

        // external contour
        objectIdea.add(new Idea("externalContour", externalContour));

        return objectIdea;
    }
}
