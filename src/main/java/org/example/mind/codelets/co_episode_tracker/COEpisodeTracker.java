package org.example.mind.codelets.co_episode_tracker;

import br.unicamp.cst.representation.idea.Category;
import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.co_episode_cat_learner.COEpisodeCategory;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class COEpisodeTracker {
    double RELEVANCE_THRESHOLD = 3;

    public Idea updateRelations(Idea sOEpisodes, Idea cOEpisodeCategories, Idea previousCOEpisodes) {

        Idea cOEpisodes = sOEpisodes.clone();

        // compare each sOEpisode;
        for (int i = 0; i < cOEpisodes.getL().size(); i++) {
            for (int j = i + 1; j < cOEpisodes.getL().size(); j++) {
                Idea ex = cOEpisodes.getL().get(i);
                Idea ey = cOEpisodes.getL().get(j);

                initializeRelations(ex);
                initializeRelations(ey);

                // check if is there a previous relation between SOEpisodes;
                boolean alreadyInARelation = alreadyHasRelation(ex, ey, previousCOEpisodes);

                if(!alreadyInARelation) {
                    // check if there is a membership across cOEpisodeCategories;
                    for(Idea categoryIdea : cOEpisodeCategories.getL()) {
                        // assign necessary relations;
                        verifyAndCreateRelationship(ex, ey, categoryIdea);
                        verifyAndCreateRelationship(ey, ex, categoryIdea);
                    }
                }
            }
        }

        // add previousCOEpisodes that are not in the current timestamp;
        ArrayList<Integer> cOEpisodesIDs = getEpisodeIds(cOEpisodes);

        ArrayList<Idea> cOEpisodesNotPresentInCurrentsOEpisodes = filterEpisodesByIdsNotInList(
                previousCOEpisodes,
                cOEpisodesIDs);

        cOEpisodes.getL().addAll(cOEpisodesNotPresentInCurrentsOEpisodes);

        return cOEpisodes;
    }


    public void verifyAndCreateRelationship(Idea ex, Idea ey, Idea categoryIdea) {
        String categoryName = categoryIdea.getName();
        COEpisodeCategory category = (COEpisodeCategory) categoryIdea.getValue();

        if (category.getRelevance() < RELEVANCE_THRESHOLD) {
            return;
        }

        Idea membershipParameters = createMembershipParameters(ex, ey);
        double isMember = category.membership(membershipParameters);

        if(isMember==1) {
            addRelation(ex, ey, categoryName, category.getRelationType());
            addRelation(ey, ex, categoryName, category.getRelationType()+"i");
        }
    }

    private Idea createMembershipParameters(Idea ex, Idea ey) {
        Idea membershipParameters = new Idea("hasRelation", "", 0);
        membershipParameters.getL().add(ex);
        membershipParameters.getL().add(ey);

        return membershipParameters;
    }

    private void addRelation(Idea source, Idea target, String categoryName, String relationType) {
        Idea relation = new Idea("relation", "", 0);
        relation.add(new Idea("eventId", (int) target.get("eventId").getValue()));
        relation.add(new Idea("category", categoryName));
        relation.add(new Idea("relationType", relationType));
        source.get("relations").add(relation);
    }

    private boolean alreadyHasRelation(Idea e1, Idea e2, Idea previousCOEpisodes) {
        return previousCOEpisodes.getL().stream()
                .anyMatch(previousEpisode -> hasRelationIn(e1, e2, previousEpisode));
    }

    private boolean hasRelationIn(Idea e1, Idea e2, Idea previousEpisode) {
        return previousEpisode.get("relations").getL().stream().
                anyMatch(relation -> matchesRelationSOEpisodeId(e1, e2, relation));
    }

    private boolean matchesRelationSOEpisodeId(Idea e1, Idea e2, Idea relation) {
        int relationId = (int) relation.get("eventId").getValue();
        int e1Id = (int) e1.get("eventId").getValue();
        int e2Id = (int) e2.get("eventId").getValue();

        if(relationId == e1Id || relationId == e2Id) {
            return true;
        }

        return false;
    }

    private ArrayList<Integer> getEpisodeIds(Idea episodesIdea) {
        // add previousCOEpisodes that are not in the current timestamp;
        ArrayList<Integer> episodesIds = (ArrayList<Integer>) episodesIdea.getL().stream()
                .map(e -> (Integer) e.get("eventId").getValue())
                .collect(Collectors.toList());
        return episodesIds;
    }

    public ArrayList<Idea> filterEpisodesByIdsNotInList(Idea episodes, ArrayList<Integer> idList) {
        return (ArrayList<Idea>) episodes.getL().stream()
                .filter(e -> !idList.contains((Integer) e.get("eventId").getValue()))
                .collect(Collectors.toList());
    }

    private void initializeRelations(Idea episode) {
        if(episode.get("relations") == null) {
            Idea relations = new Idea("relations", "", 0);
            episode.add(relations);
        }
    }
}
