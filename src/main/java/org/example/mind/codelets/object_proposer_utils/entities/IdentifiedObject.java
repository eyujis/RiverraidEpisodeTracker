package org.example.mind.codelets.object_proposer_utils.entities;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import java.util.List;
import java.util.Random;

public class IdentifiedObject {
    private double[] color;
    private List<MatOfPoint> contours;
    private MatOfPoint externalContour;
    private Rect boundRect;
    private Point centerPoint;

    private static int id = 0;
    private int objectId;
    private Scalar colorIdScalar;

    public IdentifiedObject(UnidentifiedObject unObj) {
        this.color = unObj.getColor();
        this.contours = unObj.getContours();
        this.externalContour = unObj.getExternalContour();
        this.boundRect = unObj.getBoundRect();
        this.centerPoint = unObj.getCenterPoint();

        initializeObjectId();
        initializeColorId();
    }

    private void initializeObjectId() {
        this.objectId = id;
        id++;
    }

    private void initializeColorId() {
        Random rng = new Random();
        // Random colors closer to white for avoiding dark contours with black background.
        this.colorIdScalar = new Scalar(rng.nextInt(231) + 25,
                rng.nextInt(231) + 25,
                rng.nextInt(231) + 25);
    }
    public Scalar getScalarColor() {
        return new Scalar(color[0], color[1], color[2]);
    }
    public List<MatOfPoint> getContours() {
        return contours;
    }

    public void updateProperties(UnidentifiedObject unObj) {
        this.color = unObj.getColor();
        this.contours = unObj.getContours();
        this.externalContour = unObj.getExternalContour();
        this.boundRect = unObj.getBoundRect();
        this.centerPoint = unObj.getCenterPoint();
    }

    public Rect getBoundRect() {
        return boundRect;
    }

    public Point getCenterPoint() {
        return centerPoint;
    }

    public MatOfPoint getExternalContour() {
        return externalContour;
    }

    public Scalar getColorIdScalar() {
        return colorIdScalar;
    }

    public double[] getColor() {
        return color;
    }
}
