package org.example.mind.codelets.object_proposer_codelet.entities;

import org.opencv.core.Scalar;

import java.util.Random;

public class IdentifiedRRObject extends RRObject {
    private static int id = 0;
    private int objectId;
    private Scalar colorIdScalar;

    public IdentifiedRRObject(UnidentifiedRRObject unObj) {
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

    public void updateProperties(UnidentifiedRRObject unObj) {
        this.color = unObj.getColor();
        this.contours = unObj.getContours();
        this.externalContour = unObj.getExternalContour();
        this.boundRect = unObj.getBoundRect();
        this.centerPoint = unObj.getCenterPoint();
    }

    public Scalar getColorIdScalar() {
        return colorIdScalar;
    }

    public int getObjectId() {
        return objectId;
    }
}
