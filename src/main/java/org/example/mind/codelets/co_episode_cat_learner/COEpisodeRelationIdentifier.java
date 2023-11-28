package org.example.mind.codelets.co_episode_cat_learner;

import br.unicamp.cst.representation.idea.Idea;

// This class based on https://en.wikipedia.org/wiki/Allen%27s_interval_algebra

public class COEpisodeRelationIdentifier {
    int PRECEDES_THRESHOLD = Integer.MAX_VALUE;

    public String identifyRelationType(Idea sOEpisodeX, Idea sOEpisodeY) {

        boolean xHasFinished = (boolean) sOEpisodeX.get("hasFinished").getValue();
        int xi = (int) sOEpisodeX.get("initialTimestamp").getValue();
        int xf = (int) sOEpisodeX.get("currentTimestamp").getValue();

        boolean yHasFinished = (boolean) sOEpisodeY.get("hasFinished").getValue();
        int yi = (int) sOEpisodeY.get("initialTimestamp").getValue();
        int yf = (int) sOEpisodeY.get("currentTimestamp").getValue();

//        if(hasPrecedesRelation(xHasFinished, xi, xf, yHasFinished, yi, yf)) {
//            return "p";
//        }
//        if(hasPrecedesRelation(yHasFinished, yi, yf, xHasFinished, xi, xf)) {
//            return "pi";
//        }
        if(hasMeetsRelation(xHasFinished, xi, xf, yHasFinished, yi, yf)) {
            return "m";
        }
        if(hasMeetsRelation(yHasFinished, yi, yf, xHasFinished, xi, xf)) {
            return "mi";
        }
//        if(hasOverlapsRelation(xHasFinished, xi, xf, yHasFinished, yi, yf)) {
//            return "o";
//        }
//        if(hasOverlapsRelation(yHasFinished, yi, yf, xHasFinished, xi, xf)) {
//            return "oi";
//        }
//        if(hasStartsRelation(xHasFinished, xi, xf, yHasFinished, yi, yf)) {
//            return "s";
//        }
//        if(hasStartsRelation(yHasFinished, yi, yf, xHasFinished, xi, xf)) {
//            return "si";
//        }
//        if(hasDuringRelation(xHasFinished, xi, xf, yHasFinished, yi, yf)) {
//            return "d";
//        }
//        if(hasDuringRelation(yHasFinished, yi, yf, xHasFinished, xi, xf)) {
//            return "di";
//        }
//        if(hasFinishesRelation(xHasFinished, xi, xf, yHasFinished, yi, yf)) {
//            return "f";
//        }
//        if(hasFinishesRelation(yHasFinished, yi, yf, xHasFinished, xi, xf)) {
//            return "fi";
//        }
//        if(hasEqualsRelation(xHasFinished, xi, xf, yHasFinished, yi, yf)) {
//            return "e";
//        }
//        if(hasEqualsRelation(yHasFinished, yi, yf, xHasFinished, xi, xf)) {
//            return "ei";
//        }

        return null;
    }

    public boolean hasPrecedesRelation(boolean xHasFinished, int xi, int xf, boolean yHasFinished, int yi, int yf) {
        if(xHasFinished
                && xf < yi
                && yi - xf <= PRECEDES_THRESHOLD) {
            return true;
        }
        return false;
    }

    public boolean hasMeetsRelation(boolean xHasFinished, int xi, int xf, boolean yHasFinished, int yi, int yf) {
        if(xHasFinished
                && xf == yi) {
            return true;
        }
        return false;
    }

    public boolean hasOverlapsRelation(boolean xHasFinished, int xi, int xf, boolean yHasFinished, int yi, int yf) {
        if(xHasFinished
                && xi < yi
                && yi < xf
                && xf < yf) {
            return true;
        }
        return false;
    }

    public boolean hasStartsRelation(boolean xHasFinished, int xi, int xf, boolean yHasFinished, int yi, int yf) {
        if(xHasFinished
                && xi == yi
                && xf < yf) {
            return true;
        }
        return false;
    }

    public boolean hasDuringRelation(boolean xHasFinished, int xi, int xf, boolean yHasFinished, int yi, int yf) {
        if(xHasFinished
                && yi < xi
                && xf < yf) {
            return true;
        }
        return false;
    }

    public boolean hasFinishesRelation(boolean xHasFinished, int xi, int xf, boolean yHasFinished, int yi, int yf) {
        if(xHasFinished
                && yHasFinished
                && yi < xi
                && xf == yf) {
            return true;
        }
        return false;
    }

    public boolean hasEqualsRelation(boolean xHasFinished, int xi, int xf, boolean yHasFinished, int yi, int yf) {
        if(xHasFinished
                && yHasFinished
                && xi == yi
                && xf == yf) {
            return true;
        }
        return false;
    }

}
