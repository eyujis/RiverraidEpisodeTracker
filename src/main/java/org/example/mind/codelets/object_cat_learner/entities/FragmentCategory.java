package org.example.mind.codelets.object_cat_learner.entities;

import br.unicamp.cst.representation.idea.Idea;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class FragmentCategory extends EntityCategory {
    static final double MIN_SHAPE_DIFF_RATIO= 0.1;
    static final double MIN_HUE_DIFF = 0.2;

    Idea fragmentPrototype;

    public FragmentCategory(Idea fragment, double relevance) {
        this.fragmentPrototype = fragment.clone();
        this.fragmentPrototype.setName("FragmentCategory");
        super.relevance = relevance;
    }

    @Override
    public Idea getInstance(List<Idea> constraints) {
        return null;
    }

    @Override
    public double membership(Idea fragmentIdea) {
        if(haveSimilarRectShape(fragmentIdea) && isSameColor(fragmentIdea) && hasSimilarContourShapes(fragmentIdea)) {
            return 1;
        }
        return 0;
    }

    private boolean haveSimilarRectShape(Idea frag) {
        double catHeight = (double) this.fragmentPrototype.get("size.height").getValue();
        double fragHeight = (double) frag.get("size.height").getValue();

        double catWidth = (double) this.fragmentPrototype.get("size.width").getValue();
        double fragWidth = (double) frag.get("size.width").getValue();

        if(isSimilarLength(catHeight, fragHeight)
                && isSimilarLength(catWidth, fragWidth)) {
            return true;
        }
        return false;
    }

    private boolean isSimilarLength(double len1, double len2) {
        double lenDiffRatio = Math.abs(len1-len2)/Math.max(len1, len2);
        if(lenDiffRatio<MIN_SHAPE_DIFF_RATIO) {return true;}
        return false;
    }

    public boolean isSameColor(Idea frag) {
        double fragR = (double) frag.get("color.R").getValue();
        double fragB = (double) frag.get("color.B").getValue();
        double fragG = (double) frag.get("color.G").getValue();

        double catR = (double) this.fragmentPrototype.get("color.R").getValue();
        double catB = (double) this.fragmentPrototype.get("color.B").getValue();
        double catG = (double) this.fragmentPrototype.get("color.G").getValue();

        if(catG==fragG
           && catB==fragB
           && catR==fragR) {
            return true;
        }
        return false;
    }

    private boolean hasSimilarContourShapes(Idea frag) {
        double hueDistance = Imgproc.matchShapes((MatOfPoint) fragmentPrototype.get("externalContour").getValue(),
                (MatOfPoint) frag.get("externalContour").getValue(),
                Imgproc.CV_CONTOURS_MATCH_I1,
                0.0);

        if (hueDistance<=MIN_HUE_DIFF) {
            return true;
        }
        return false;
    }
}
