package org.example.mind.codelets.co_episode_cat_learner;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.co_episode_tracker.Coupling;
import org.example.mind.codelets.object_proposer.ObjectComparator;

import java.util.stream.Collectors;

public class COEpisodeCategoryLearner {
    double RELEVANCE_THRESHOLD = 3;
//    double INIT_RELEVANCE = 1;
    double INIT_RELEVANCE = 5;
    double INCREMENT_FACTOR = 2;
    double DECREMENT_FACTOR = 1;
    double MINIMUM_RELEVANCE = 0;
    double MIN_RECT_DISTANCE = 2;

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

    public Idea extractCOEpisodeCategories(Idea sOEpisodes) {
        Idea rcvCOEpisodeCategories = new Idea("extractedCOEpisodeCategories", "", 0);

        for (int i = 0; i < sOEpisodes.getL().size(); i++) {
            for (int j = i + 1; j < sOEpisodes.getL().size(); j++) {
                Idea e1 = sOEpisodes.getL().get(i);
                Idea e2 = sOEpisodes.getL().get(j);

                String relationType = identifyCOEpisodeCategoryRelation(e1, e2);
                String c1 = (String) e1.get("eventCategory").getValue();
                String c2 = (String) e2.get("eventCategory").getValue();

                if(relationType != null
                        && (Coupling.haveCouplingConditions(e1, e2, relationType) || sameObjectId(e1, e2))) {
                    Idea newCategory = cOEpisodeCategoryFactory.createCOEpisodeCategory(relationType, c1, c2, INIT_RELEVANCE);
                    rcvCOEpisodeCategories.getL().add(newCategory);
                }
            }
        }
        return rcvCOEpisodeCategories;
    }

    public String identifyCOEpisodeCategoryRelation(Idea sOEpisode1, Idea sOEpisode2) {
        COEpisodeRelationIdentifier relationIdentifier = new COEpisodeRelationIdentifier();
        String relation = relationIdentifier.identifyRelationType(sOEpisode1, sOEpisode2);
        return relation;
    }

    public int equalCategoryIdx(Idea categoryFromInstance, Idea listOfCategories) {
        int idx = -1;

        for(int i = 0; i < listOfCategories.getL().size(); i++) {
            COEpisodeCategory cat = (COEpisodeCategory) listOfCategories.getL().get(i).getValue();
            COEpisodeCategory instanceCat = (COEpisodeCategory) categoryFromInstance.getValue();

            if(cat.sameCategory(instanceCat) == true) {
                idx = i;
            }
        }
        return idx;
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
