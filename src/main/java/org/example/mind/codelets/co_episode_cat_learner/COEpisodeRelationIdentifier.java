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

        if(hasPrecedesRelation(xHasFinished, xi, xf, yHasFinished, yi, xf)) {
            return "p";
        }
        if(hasPrecedesRelation(yHasFinished, yi, yf, xHasFinished, xi, xf)) {
            return "pi";
        }

        return null;
    }

    private boolean hasPrecedesRelation(boolean xHasFinished, int xi, int xf, boolean yHasFinished, int yi, int yf) {
        if(xHasFinished
                && xf < yi
                && yi - xf <= PRECEDES_THRESHOLD) {
            return true;
        }
        return false;
    }

    private boolean hasMeetsRelation(boolean xHasFinished, int xi, int xf, boolean yHasFinished, int yi, int yf) {
        if(xHasFinished
                && xf == yi) {
            return true;
        }
        return false;
    }

    private boolean hasOverlapsRelation(boolean xHasFinished, int xi, int xf, boolean yHasFinished, int yi, int yf) {
        if(xHasFinished
                && xi < yi
                && yi < xf
                && xf < yf) {
            return true;
        }
        return false;
    }

    private boolean hasStartsRelation(boolean xHasFinished, int xi, int xf, boolean yHasFinished, int yi, int yf) {
        if(xHasFinished
                && xi == yi
                && xf < yf) {
            return true;
        }
        return false;
    }

    private boolean hasDuringRelation(boolean xHasFinished, int xi, int xf, boolean yHasFinished, int yi, int yf) {
        if(xHasFinished
                && yi < xi
                && xf < yf) {
            return true;
        }
        return false;
    }

    private boolean hasFinishesRelation(boolean xHasFinished, int xi, int xf, boolean yHasFinished, int yi, int yf) {
        if(xHasFinished
                && yHasFinished
                && yi < xi
                && xf == yf) {
            return true;
        }
        return false;
    }

    private boolean hasEqualsRelation(boolean xHasFinished, int xi, int xf, boolean yHasFinished, int yi, int yf) {
        if(xHasFinished
                && yHasFinished
                && xi == yi
                && xf == yf) {
            return true;
        }
        return false;
    }

}
