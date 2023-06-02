package org.example.mind.codelets.object_proposer_codelet;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_proposer_codelet.entities.RRObject;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

public class ObjectComparator {
    static final double MIN_CENTER_DISTANCE = 50;
    static final double MIN_SHAPE_DIFF_RATIO= 0.3;
    static final double MIN_HUE_DIFF = 2;

    public boolean similarPolygonShape(RRObject o1, RRObject o2) {
        double hueDistance = Imgproc.matchShapes(o1.getExternalContour(),
                o2.getExternalContour(),
                Imgproc.CV_CONTOURS_MATCH_I1,
                0.0);
        if(hueDistance<=MIN_HUE_DIFF) {
            return true;
        }
        return false;
    }

    public double getHueDistance(RRObject o1, RRObject o2) {
        return Imgproc.matchShapes(o1.getExternalContour(),
                o2.getExternalContour(),
                Imgproc.CV_CONTOURS_MATCH_I1,
                0.0);
    }


    public boolean closeCenterDistance(RRObject o1, RRObject o2) {
        double centerDistance = getCenterDistance(o1, o2);
        if(centerDistance<=MIN_CENTER_DISTANCE) {
            return true;
        }
        return false;
    }

    public boolean areSameColor(RRObject o1, RRObject o2) {
        double[] color1 = o1.getColor();
        double[] color2 = o2.getColor();
        if(color1[0]==color2[0]
                && color1[1]==color2[1]
                && color1[2]==color2[2]) {
            return true;
        }
        return false;
    }

    public double getCenterDistance(RRObject o1, RRObject o2) {
        return pointDistance(o1.getCenterPoint(), o2.getCenterPoint());
    }

    public double pointDistance(Point p1, Point p2) {
        double xSquared = Math.pow(p1.x-p2.x,2);
        double ySquared = Math.pow(p1.y-p2.y,2);
        return (Math.sqrt(xSquared+ySquared));
    }

    public boolean haveSimilarRectShape(RRObject obj1, RRObject obj2) {
        double obj1Height = obj1.getBoundRect().height;
        double obj2Height = obj2.getBoundRect().height;

        double obj1Width = obj1.getBoundRect().width;
        double obj2Width = obj2.getBoundRect().width;

        if(hasSimilarLength(obj1Height, obj2Height)
                && hasSimilarLength(obj1Width, obj2Width)) {
            return true;
        }
        return false;
    }

    private boolean hasSimilarLength(double len1, double len2) {
        double lenDiffRatio = Math.abs(len1-len2)/Math.max(len1, len2);
        if(lenDiffRatio<MIN_SHAPE_DIFF_RATIO) {return true;}
        return false;
    }

    public double rectDistance(RRObject obj1, RRObject obj2) {
        Rect rect1 = obj1.getBoundRect();
        Rect rect2 = obj2.getBoundRect();

        // based on https://stackoverflow.com/a/26178015
        double x1tl = rect1.tl().x;
        double y1tl = rect1.tl().y;
        double x1br = rect1.br().x;
        double y1br = rect1.br().y;
        double x2tl = rect2.tl().x;
        double y2tl = rect2.tl().y;
        double x2br = rect2.br().x;
        double y2br = rect2.br().y;

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

    public double rectDistance(Idea obj1, Idea obj2) {

        // based on https://stackoverflow.com/a/26178015
        double x1tl = (double) obj1.get("boundRect.tl.x").getValue();
        double y1tl = (double) obj1.get("boundRect.tl.y").getValue();
        double x1br = (double) obj1.get("boundRect.br.x").getValue();
        double y1br = (double) obj1.get("boundRect.br.y").getValue();
        double x2tl = (double) obj2.get("boundRect.tl.x").getValue();
        double y2tl = (double) obj2.get("boundRect.tl.y").getValue();
        double x2br = (double) obj2.get("boundRect.br.x").getValue();
        double y2br = (double) obj2.get("boundRect.br.y").getValue();

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
