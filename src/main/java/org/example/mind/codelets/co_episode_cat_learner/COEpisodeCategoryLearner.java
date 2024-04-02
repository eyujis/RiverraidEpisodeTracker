package org.example.mind.codelets.co_episode_cat_learner;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.co_episode_tracker.Coupling;
import org.example.mind.codelets.object_proposer.ObjectComparator;

import java.util.stream.Collectors;

public class COEpisodeCategoryLearner {
    double RELEVANCE_THRESHOLD = 3; // remembers every category learned
    double INCREMENT_FACTOR = 2;
    double DECREMENT_FACTOR = 0;
    double MINIMUM_RELEVANCE = 0;

    COEpisodeCategoryFactory cOEpisodeCategoryFactory;

    public COEpisodeCategoryLearner() {
        cOEpisodeCategoryFactory = new COEpisodeCategoryFactory();
    }

    public Idea updateCategories(Idea sOEpisodes, Idea cOEpisodeCategories) {
        Idea updatedCategories = cOEpisodeCategories.clone();

        for (int i = 0; i < sOEpisodes.getL().size(); i++) {
            for (int j = i + 1; j < sOEpisodes.getL().size(); j++) {
                Idea ex = sOEpisodes.getL().get(i);
                Idea ey = sOEpisodes.getL().get(j);

                for(Idea categoryIdea : cOEpisodeCategories.getL()) {
                    if(isMember(ex, ey, categoryIdea, sOEpisodes)
                    || isMember(ey, ex, categoryIdea, sOEpisodes)) {
                        COEpisodeCategory category = (COEpisodeCategory) categoryIdea.getValue();
                        if(category.getRelevance() < RELEVANCE_THRESHOLD) {
                            category.incrementRelevance(INCREMENT_FACTOR);
                        };
                    }
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
        return relevance < MINIMUM_RELEVANCE;
    }

    public boolean isMember(Idea ex, Idea ey, Idea categoryIdea, Idea rcvEpisodes) {
        COEpisodeCategory category = (COEpisodeCategory) categoryIdea.getValue();

        Idea membershipParameters = createMembershipParameters(ex, ey, rcvEpisodes);
        double isMember = category.membership(membershipParameters);

        return isMember == 1;
    }

    private Idea createMembershipParameters(Idea ex, Idea ey, Idea previousEpisodes) {
        Idea membershipParameters = new Idea("hasRelation", "", 0);
        membershipParameters.getL().add(ex);
        membershipParameters.getL().add(ey);
        membershipParameters.getL().add(previousEpisodes);

        return membershipParameters;
    }
}
