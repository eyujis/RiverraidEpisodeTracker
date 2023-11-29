package org.example.mind.codelets.co_episode_tracker;

import br.unicamp.cst.representation.idea.Category;
import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.co_episode_cat_learner.COEpisodeCategory;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class COEpisodeTracker {
    public Idea updateRelations(Idea sOEpisodes, Idea cOEpisodeCategories, Idea previousCOEpisodes) {

        // compare each sOEpisode;
        // check if is there a previous relation between SOEpisodes;
        // check if there is a membership across cOEpisodeCategories;
        // assign necessary relations;
        // add previousCOEpisodes that vanished in the current timestamp;

        Idea cOEpisodes = sOEpisodes.clone();


        for (int i = 0; i < cOEpisodes.getL().size(); i++) {
            for (int j = i + 1; j < cOEpisodes.getL().size(); j++) {
                Idea ex = cOEpisodes.getL().get(i);
                Idea ey = cOEpisodes.getL().get(j);

                initializeRelations(ex);
                initializeRelations(ey);

                boolean alreadyInARelation = alreadyHasRelation(ex, ey, previousCOEpisodes);

                if(!alreadyInARelation) {
                    for(Idea categoryIdea : cOEpisodeCategories.getL()) {
                        String categoryName = categoryIdea.getName();
                        COEpisodeCategory category = (COEpisodeCategory) categoryIdea.getValue();

                        Idea membershipParameters = new Idea("hasRelation", "", 0);
                        membershipParameters.getL().add(ex);
                        membershipParameters.getL().add(ey);

                        double isMember = category.membership(membershipParameters);
                        if(isMember==1) {
                            Idea eyRelation = new Idea("relation", "", 0);
                            eyRelation.add(new Idea("eventId", (int) ey.get("eventId").getValue()));
                            eyRelation.add(new Idea("category", categoryName));
                            eyRelation.add(new Idea("relationType", category.getRelationType()));
                            ex.get("relations").add(eyRelation);

                            Idea exRelation = new Idea("relation", "", 0);
                            exRelation.add(new Idea("eventId", (int) ex.get("eventId").getValue()));
                            exRelation.add(new Idea("category", categoryName));
                            exRelation.add(new Idea("relationType", category.getRelationType() + "i"));
                            ey.get("relations").add(exRelation);
                        }

                        Idea membershipIParameters = new Idea("hasRelationI", "", 0);
                        membershipIParameters.getL().add(ey);
                        membershipIParameters.getL().add(ex);

                        double isIMember = category.membership(membershipIParameters);
                        if(isIMember==1) {
                            Idea exRelation = new Idea("relation", "", 0);
                            exRelation.add(new Idea("eventId", (int) ex.get("eventId").getValue()));
                            exRelation.add(new Idea("category", categoryName));
                            exRelation.add(new Idea("relationType", category.getRelationType()));
                            ey.get("relations").add(exRelation);

                            Idea eyRelation = new Idea("relation", "", 0);
                            eyRelation.add(new Idea("eventId", (int) ey.get("eventId").getValue()));
                            eyRelation.add(new Idea("category", categoryName));
                            eyRelation.add(new Idea("relationType", category.getRelationType() + "i"));
                            ex.get("relations").add(eyRelation);
                        }

                    }
                }
            }
        }

        ArrayList<Integer> cOEpisodesIDs = (ArrayList<Integer>) cOEpisodes.getL().stream()
                .map(e -> (Integer) e.get("eventId").getValue())
                .collect(Collectors.toList());

        ArrayList<Idea> cOEpisodesNotPresentInCurrentsOEpisodes = (ArrayList<Idea>) previousCOEpisodes.getL().stream()
                .filter(e -> !cOEpisodesIDs.contains((Integer) e.get("eventId").getValue()))
                .collect(Collectors.toList());

        cOEpisodes.getL().addAll(cOEpisodesNotPresentInCurrentsOEpisodes);

        return cOEpisodes;
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

    private void initializeRelations(Idea episode) {
        if(episode.get("relations") == null) {
            Idea relations = new Idea("relations", "", 0);
            episode.add(relations);
        }
    }
}
