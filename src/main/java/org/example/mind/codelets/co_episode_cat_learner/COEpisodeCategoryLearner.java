package org.example.mind.codelets.co_episode_cat_learner;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.co_episode_tracker.Coupling;
import org.example.mind.codelets.object_proposer.ObjectComparator;

import java.util.stream.Collectors;

public class COEpisodeCategoryLearner {
    double RELEVANCE_THRESHOLD = 3;
    double INIT_RELEVANCE = 5;
    double INCREMENT_FACTOR = 2;
    double DECREMENT_FACTOR = 0;
    double MINIMUM_RELEVANCE = 0;

    COEpisodeCategoryFactory cOEpisodeCategoryFactory;

    public COEpisodeCategoryLearner() {
        cOEpisodeCategoryFactory = new COEpisodeCategoryFactory();
    }

    public Idea updateCategories(Idea sOEpisodes, Idea cOEpisodeCategories) {
        Idea updatedCategories = cOEpisodeCategories.clone();

        Idea rcvCOEpisodeCategories = extractCOEpisodeCategories(sOEpisodes);

        for(Idea rcvCategory : rcvCOEpisodeCategories.getL()) {
            int equalCatIdx = equalCategoryIdx(rcvCategory, updatedCategories);

            if(equalCatIdx == -1) {
//                updatedCategories.add(rcvCategory);
            } else {
                COEpisodeCategory category = (COEpisodeCategory) updatedCategories.getL().get(equalCatIdx).getValue();
                if (category.getRelevance()<RELEVANCE_THRESHOLD) {
                    category.incrementRelevance(INCREMENT_FACTOR);
                }
            }
        }

        decrementCategoriesRelevance(updatedCategories);
        updatedCategories = filterIrrelevantCategories(updatedCategories);

        return updatedCategories;
    }

    public void decrementCategoriesRelevance(Idea categoryList) {
        for(Idea categoryIdea: categoryList.getL()) {
            COEpisodeCategory category = (COEpisodeCategory) categoryIdea.getValue();
            if(category.getRelevance() < RELEVANCE_THRESHOLD) {
                category.decrementRelevance(DECREMENT_FACTOR);
            }
        }
    }

    public Idea filterIrrelevantCategories(Idea categoryList) {
        Idea filteredCategories = new Idea("FilteredSOEpisodeCategories", "", 0);
        filteredCategories.getL().addAll(categoryList.getL().stream()
                .filter(categoryIdea -> !belowMinimumRelevance(categoryIdea))
                .collect(Collectors.toList()));
        return filteredCategories;
    }

    public boolean belowMinimumRelevance(Idea categoryIdea) {
        COEpisodeCategory category = (COEpisodeCategory) categoryIdea.getValue();
        double relevance = category.getRelevance();
        if(relevance < MINIMUM_RELEVANCE) {
            return true;
        } else {
            return false;
        }
    }

    public boolean sameObjectId(Idea ex, Idea ey) {
        int objectIdx = (int) ex.get("objectId").getValue();
        int objectIdy = (int) ey.get("objectId").getValue();

        return objectIdx==objectIdy;
    }
}
