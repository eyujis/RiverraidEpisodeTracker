package org.example.mind.codelets.object_proposer_codelet;

import br.unicamp.cst.representation.idea.Idea;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

public class ObjectComparator {
    static final double MIN_CENTER_DISTANCE = 50;
    static final double MIN_SHAPE_DIFF_RATIO= 0.30;

    public boolean closeCenterDistance(Idea f1, Idea f2) {
        double centerDistance = getCenterDistance(f1, f2);
        if(centerDistance<=MIN_CENTER_DISTANCE) {
            return true;
        }
        return false;
    }


    public double getCenterDistance(Idea f1, Idea f2) {
        double x1 = (double) f1.get("center.x").getValue();
        double x2 = (double) f2.get("center.x").getValue();
        double y1 = (double) f1.get("center.y").getValue();
        double y2 = (double) f2.get("center.y").getValue();

        return pointDistance(new Point(x1, y1), new Point(x2, y2));
    }

    public double pointDistance(Point p1, Point p2) {
        double xSquared = Math.pow(p1.x-p2.x,2);
        double ySquared = Math.pow(p1.y-p2.y,2);
        return (Math.sqrt(xSquared+ySquared));
    }

    public boolean haveSimilarRectShape(Idea f1, Idea f2) {
        double f1Height = (double) f1.get("boundRect.height").getValue();
        double f2Height = (double) f2.get("boundRect.height").getValue();

        double f1Width = (double) f1.get("boundRect.width").getValue();
        double f2Width = (double) f2.get("boundRect.width").getValue();

        if(hasSimilarLength(f1Height, f2Height)
                && hasSimilarLength(f1Width, f2Width)) {
            return true;
        }
        return false;
    }

    private boolean hasSimilarLength(double len1, double len2) {
        double meanLen = (len1+len2)/2;
        double lenDiffRatio = Math.abs(len1-len2)/meanLen;
        if(lenDiffRatio<MIN_SHAPE_DIFF_RATIO) {return true;}
        return false;
    }

}
