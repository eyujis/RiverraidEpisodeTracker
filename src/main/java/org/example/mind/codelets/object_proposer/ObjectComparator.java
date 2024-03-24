package org.example.mind.codelets.object_proposer;

import br.unicamp.cst.representation.idea.Idea;
import org.opencv.core.Point;

public class ObjectComparator {
    static final double MIN_CENTER_DISTANCE = 25;
    static final double MIN_SHAPE_DIFF_RATIO= 0.5;

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
        return rectShapeSimilarity(f1, f2) > MIN_SHAPE_DIFF_RATIO;
    }

    public double rectShapeSimilarity(Idea f1, Idea f2) {
        double f1Height = (double) f1.get("size.height").getValue();
        double f2Height = (double) f2.get("size.height").getValue();

        double f1Width = (double) f1.get("size.width").getValue();
        double f2Width = (double) f2.get("size.width").getValue();

        double heightSimilarity = calculateSimilarity(f1Height, f2Height);
        double widthSimilarity = calculateSimilarity(f1Width, f2Width);

        return (heightSimilarity + widthSimilarity) / 2.0;
    }

    private double calculateSimilarity(double len1, double len2) {
        double maxValue = Math.max(len1, len2);
        return 1.0 - (Math.abs(len1 - len2) / maxValue);
    }

    public double rectDistance(Idea o1, Idea o2) {

        // based on https://stackoverflow.com/a/26178015
        double x1tl = (double) o1.get("boundRect.tl.x").getValue();
        double y1tl = (double) o1.get("boundRect.tl.y").getValue();
        double x1br = (double) o1.get("boundRect.br.x").getValue();
        double y1br = (double) o1.get("boundRect.br.y").getValue();
        double x2tl = (double) o2.get("boundRect.tl.x").getValue();
        double y2tl = (double) o2.get("boundRect.tl.y").getValue();
        double x2br = (double) o2.get("boundRect.br.x").getValue();
        double y2br = (double) o2.get("boundRect.br.y").getValue();

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
            return Math.abs(x1tl - x2br);
        } else if(right) {
            return Math.abs(x2tl - x1br);
        } else if(bottom) {
            return Math.abs(y1tl - y2br);
        } else if(top) {
            return Math.abs(y2tl - y1br);
        } else {
            return 0;
        }
    }
}
