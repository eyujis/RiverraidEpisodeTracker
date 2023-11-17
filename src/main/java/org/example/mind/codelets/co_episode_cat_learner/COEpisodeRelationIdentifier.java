package org.example.mind.codelets.co_episode_cat_learner;

import br.unicamp.cst.representation.idea.Idea;

// This class based on https://en.wikipedia.org/wiki/Allen%27s_interval_algebra

public class COEpisodeRelationIdentifier {
    int PRECEDES_THRESHOLD = 5;

    public String identifyRelationType(Idea sOEpisodeX, Idea sOEpisodeY) {

        boolean xHasFinished = (boolean) sOEpisodeX.get("hasFinished").getValue();
        int xIT = (int) sOEpisodeX.get("initialTimestamp").getValue();
        int xCT = (int) sOEpisodeX.get("currentTimestamp").getValue();

        boolean yHasFinished = (boolean) sOEpisodeY.get("hasFinished").getValue();
        int yIT = (int) sOEpisodeY.get("initialTimestamp").getValue();
        int yCT = (int) sOEpisodeY.get("currentTimestamp").getValue();

        if(hasMeetsRelation(xHasFinished, xIT, xCT, yHasFinished, yIT, yCT)) {
            return "m";
        }
        if(hasMeetsRelation(yHasFinished, yIT, yCT, xHasFinished, xIT, xCT)) {
            return "mi";
        }

        return null;
    }

    private boolean hasMeetsRelation(boolean xHasFinished, int xIT, int xCT, boolean yHasFinished, int yIT, int yCT) {
        if(xHasFinished == true
            && xCT == yIT) {
            return true;
        }
        return false;
    }


}
