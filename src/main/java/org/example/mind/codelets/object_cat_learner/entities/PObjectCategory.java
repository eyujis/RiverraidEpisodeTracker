package org.example.mind.codelets.object_cat_learner.entities;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_proposer_codelet.entities.RRObject;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;
import java.util.Random;

public class PObjectCategory extends ObjectCategory {
    static final double MIN_SHAPE_DIFF_RATIO= 0.1;
    static final double MIN_HUE_DIFF = 0.2;

    Idea objPrototype;

    public PObjectCategory(Idea obj, double relevance) {
        this.objPrototype = obj.clone();
        this.objPrototype.setName("category");
        super.relevance = relevance;

        super.initializeCategoryId();
        super.initializeColorId();
    }

    @Override
    public Idea getInstance(List<Idea> constraints) {
        return null;
    }

    @Override
    public double membership(Idea objIdea) {
        if(haveSimilarRectShape(objIdea) && isSameColor(objIdea) && hasSimilarContourShapes(objIdea)) {
            return 1;
        }
        return 0;
    }

    private boolean haveSimilarRectShape(Idea obj) {
        double catHeight = (int) this.objPrototype.get("boundRect.height").getValue();
        double objHeight = (int) obj.get("boundRect.height").getValue();

        double catWidth = (int) this.objPrototype.get("boundRect.width").getValue();
        double objWidth = (int) obj.get("boundRect.width").getValue();

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

    public boolean isSameColor(Idea obj) {
        double objR = (double) obj.get("color.R").getValue();
        double objB = (double) obj.get("color.B").getValue();
        double objG = (double) obj.get("color.G").getValue();

        double catR = (double) this.objPrototype.get("color.R").getValue();
        double catB = (double) this.objPrototype.get("color.B").getValue();
        double catG = (double) this.objPrototype.get("color.G").getValue();

        if(catG==objG
           && catB==objB
           && catR==objR) {
            return true;
        }
        return false;
    }

    private boolean hasSimilarContourShapes(Idea obj) {
        double hueDistance = Imgproc.matchShapes((MatOfPoint) objPrototype.get("externalContour").getValue(),
                (MatOfPoint) obj.get("externalContour").getValue(),
                Imgproc.CV_CONTOURS_MATCH_I1,
                0.0);

        if (hueDistance<=MIN_HUE_DIFF) {
            return true;
        }
        return false;
    }
}
