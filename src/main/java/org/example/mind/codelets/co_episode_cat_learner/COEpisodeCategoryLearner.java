package org.example.mind.codelets.co_episode_cat_learner;

import br.unicamp.cst.representation.idea.Idea;

public class COEpisodeCategoryLearner {
    Idea cOEpisodeCategoryList;
    COEpisodeCategoryFactory cOEpisodeCategoryFactory;

    public COEpisodeCategoryLearner() {
        cOEpisodeCategoryList = new Idea("COEpisodeCategories", "", 0);
        cOEpisodeCategoryFactory = new COEpisodeCategoryFactory();
    }

    public void updateCategories(Idea sOEpisodes, Idea cOEpisodeCategories) {
        Idea rcvCOEpisodeCategories = extractCOEpisodeCategories(sOEpisodes);
        
    }

    public Idea extractCOEpisodeCategories(Idea sOEpisodes) {
        for (int i = 0; i < sOEpisodes.getL().size(); i++) {
            for (int j = i + 1; j < sOEpisodes.getL().size(); j++) {

            }
        }
        return new Idea();
    }

    public Idea getRelevantCategories() {
        Idea relevantCategories = new Idea("RelevantCategories", "", 0);
        // TODO add code to filter relevant categories
        return relevantCategories;
    }
}
