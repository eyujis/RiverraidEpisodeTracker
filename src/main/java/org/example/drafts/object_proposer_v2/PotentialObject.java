package org.example.drafts.object_proposer_v2;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.Random;

public class PotentialObject {
    private Rect boundRect;
    private Point centerPoint;
    private MatOfPoint contour;

    private Random rng = new Random();
    // Random colors closer to white for avoiding dark contours with black background.
    private Scalar color = new Scalar(rng.nextInt(231) + 25,
                                      rng.nextInt(231) + 25,
                                      rng.nextInt(231) + 25);

    private static int id = 0;
    private int objectId;

    public PotentialObject(MatOfPoint contour) {
        initializeObjectId();

        this.contour = contour;

        MatOfPoint2f contourPoly = new MatOfPoint2f();
        Imgproc.approxPolyDP(new MatOfPoint2f(contour.toArray()), contourPoly, 3, true);
        boundRect = Imgproc.boundingRect(new MatOfPoint(contourPoly.toArray()));

        setCenterFromBoundRect();
    }

    public void updateObjectToCF(PotentialObject objectCF) {
        this.contour = objectCF.getContour();
        this.centerPoint = objectCF.getCenterPoint();
        this.boundRect = objectCF.getBoundRect();
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

    public void initializeObjectId() {
        this.objectId = id;
        incrementId();
    }

    private void incrementId() {
        id++;
    }

    public Scalar getColor() {
        return color;
    }

    public MatOfPoint getContour() {
        return contour;
    }
}
