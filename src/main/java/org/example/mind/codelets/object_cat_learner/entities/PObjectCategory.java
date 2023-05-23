package org.example.mind.codelets.object_cat_learner.entities;

import org.example.mind.codelets.object_proposer_codelet.entities.RRObject;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.Random;

public class PObjectCategory extends ObjectCategory {
    static final double MIN_SHAPE_DIFF_RATIO= 0.1;
    static final double MIN_HUE_DIFF = 0.2;

    private MatOfPoint externalContour;
    private Rect boundRect;
    private double[] color;


    public PObjectCategory(MatOfPoint externalContour, Rect boundRect, double[] color, double relevance) {
        this.externalContour = externalContour;
        this.boundRect = boundRect;
        this.color = color;
        super.relevance = relevance;

        super.initializeCategoryId();
        super.initializeColorId();
    }

    public double membership(RRObject obj) {
        if(haveSimilarRectShape(obj) && isSameColor(obj) && hasSimilarContourShapes(obj)) {
            return 1;
        }
        return 0;
    }

    private boolean haveSimilarRectShape(RRObject obj) {
        double catHeight = this.boundRect.height;
        double objHeight = obj.getBoundRect().height;

        double catWidth = this.boundRect.width;
        double objWidth = obj.getBoundRect().width;

        if(isSimilarLength(catHeight, objHeight)
                && isSimilarLength(catWidth, objWidth)) {
            return true;
        }
        return false;
    }

    private boolean isSimilarLength(double len1, double len2) {
        double lenDiffRatio = Math.abs(len1-len2)/Math.max(len1, len2);
        if(lenDiffRatio<MIN_SHAPE_DIFF_RATIO) {return true;}
        return false;
    }

    public boolean isSameColor(RRObject obj) {
        double [] objColor = obj.getColor();
        if(this.color[0]==objColor[0]
                && this.color[1]==objColor[1]
                && this.color[2]==objColor[2]) {
            return true;
        }
        return false;
    }

    private boolean hasSimilarContourShapes(RRObject obj) {
        double hueDistance = Imgproc.matchShapes(this.externalContour,
                obj.getExternalContour(),
                Imgproc.CV_CONTOURS_MATCH_I1,
                0.0);

        if (hueDistance<=MIN_HUE_DIFF) {
            return true;
        }
        return false;
    }

}
