package org.example.mind.codelets.object_proposer_codelet;

import br.unicamp.cst.representation.idea.Idea;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

public class FragmentComparator {
    static final double MIN_CENTER_DISTANCE = 50;
    static final double MIN_SHAPE_DIFF_RATIO= 0.3;
    static final double MIN_HUE_DIFF = 2;

    public double getHueDistance(Idea f1, Idea f2) {
        MatOfPoint contour1 = (MatOfPoint) f1.get("externalContour").getValue();
        MatOfPoint contour2 = (MatOfPoint) f2.get("externalContour").getValue();

        return Imgproc.matchShapes(contour1,
                                   contour2,
                                   Imgproc.CV_CONTOURS_MATCH_I1,
                          0.0);
    }


    public boolean closeCenterDistance(Idea f1, Idea f2) {
        double centerDistance = getCenterDistance(f1, f2);
        if(centerDistance<=MIN_CENTER_DISTANCE) {
            return true;
        }
        return false;
    }

    public boolean areSameColor(Idea f1, Idea f2) {
        double R1 = (double) f1.get("color.R").getValue();
        double B1 = (double) f1.get("color.B").getValue();
        double G1 = (double) f1.get("color.G").getValue();

        double R2 = (double) f2.get("color.R").getValue();
        double B2 = (double) f2.get("color.B").getValue();
        double G2 = (double) f2.get("color.G").getValue();

        if(R1==R2 && B1==B2 && G1==G2) {
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
        double lenDiffRatio = Math.abs(len1-len2)/Math.max(len1, len2);
        if(lenDiffRatio<MIN_SHAPE_DIFF_RATIO) {return true;}
        return false;
    }

    public double rectDistance(Idea f1, Idea f2) {

        // based on https://stackoverflow.com/a/26178015
        double x1tl = (double) f1.get("boundRect.tl.x").getValue();
        double y1tl = (double) f1.get("boundRect.tl.y").getValue();
        double x1br = (double) f1.get("boundRect.br.x").getValue();
        double y1br = (double) f1.get("boundRect.br.y").getValue();
        double x2tl = (double) f2.get("boundRect.tl.x").getValue();
        double y2tl = (double) f2.get("boundRect.tl.y").getValue();
        double x2br = (double) f2.get("boundRect.br.x").getValue();
        double y2br = (double) f2.get("boundRect.br.y").getValue();

        boolean left = x2br < x1tl;
        boolean right = x1br < x2tl;
        boolean bottom = y2br < y1tl;
        boolean top = y1br < y2tl;

        if(top && left) {
            return pointDistance(new Point(x1tl, y1br), new Point(x2br, y2tl));
        } else if(left && bottom) {
            return pointDistance(new Point(x1tl, y1tl), new Point(x2br, y2br));
        } else if(bottom && right) {
            return pointDistance(new Point(x1br, y1tl), new Point(x2tl, y2br));
        } else if(right && top) {
            return pointDistance(new Point(x1br, y1br), new Point(x2tl, y2tl));
        } else if(left) {
            return x1tl - x2br;
        } else if(right) {
            return x2tl - x1br;
        } else if(bottom) {
            return y1tl - y2br;
        } else if(top) {
            return y2tl - y1br;
        } else {
            return 0;
        }
    }
}
