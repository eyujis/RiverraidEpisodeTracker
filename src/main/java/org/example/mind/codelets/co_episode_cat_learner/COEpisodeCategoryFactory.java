package org.example.mind.codelets.co_episode_cat_learner;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.event_cat_learner.AppearanceEventCategory;
import org.example.mind.codelets.event_cat_learner.VectorEventCategory;

public class COEpisodeCategoryFactory {
    static int factoryId = 0;

    public Idea createCOEpisodeCategory(String relationType, String sOEpisodeCategoryX, String sOEpisodeCategoryY, double relevance) {
        Idea category;

        if(relationType.endsWith("i")) {
            String iLessRelationType = relationType.substring(0, 1);
            category = new Idea("COEpisodeCategory"+ generateEventId(),
                    new COEpisodeCategory(iLessRelationType, sOEpisodeCategoryY, sOEpisodeCategoryX, relevance));
        } else {
            category = new Idea("COEpisodeCategory"+ generateEventId(),
                    new COEpisodeCategory(relationType, sOEpisodeCategoryX, sOEpisodeCategoryY, relevance));
        }

        return category;
    }

    public int generateEventId() {
        factoryId++;
        return factoryId;
    }
}
