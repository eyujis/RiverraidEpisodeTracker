package org.example.mind.codelets.co_episode_tracker;

import br.unicamp.cst.representation.idea.Category;
import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.co_episode_cat_learner.COEpisodeCategory;
import org.example.mind.codelets.co_episode_cat_learner.COEpisodeCategoryFactory;
import org.example.mind.codelets.co_episode_cat_learner.COEpisodeRelationIdentifier;
import org.example.mind.codelets.object_proposer.ObjectComparator;
import org.example.visualization.Category2Color;
import org.example.visualization.Relation2Color;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

public class COEpisodeTracker {
    double RELEVANCE_THRESHOLD = 3;
    double MIN_RECT_DISTANCE = 2;
    double INIT_RELEVANCE = 5;

    COEpisodeCategoryFactory cOEpisodeCategoryFactory;

    public Idea updateRelations(Idea sOEpisodes, Idea cOEpisodeCategories, Idea previousCOEpisodes) {
        cOEpisodeCategoryFactory = new COEpisodeCategoryFactory();
        Idea cOEpisodes = sOEpisodes.clone();
        initializeRelations(cOEpisodes);
        duplicateRelations(cOEpisodes, previousCOEpisodes);

        // compare each sOEpisode;
        for (int i = 0; i < cOEpisodes.getL().size(); i++) {
            for (int j = i + 1; j < cOEpisodes.getL().size(); j++) {
                Idea ex = cOEpisodes.getL().get(i);
                Idea ey = cOEpisodes.getL().get(j);

                // check if is there a previous relation between SOEpisodes;
                boolean alreadyInARelation = assignPreviousRelationsIfThereAre(ex, ey, previousCOEpisodes);

                if(!alreadyInARelation) {
                    boolean foundCategory = false;
                    boolean foundICategory = false;

                    // check if there is a membership across cOEpisodeCategories;
                    for(Idea categoryIdea : cOEpisodeCategories.getL()) {
                        // assign necessary relations;
                        foundCategory = verifyAndCreateRelationship(ex, ey, categoryIdea);
                        foundICategory = verifyAndCreateRelationship(ey, ex, categoryIdea);
                    }

                    if(!foundCategory && !foundICategory) {
                        String relationType = identifyCOEpisodeCategoryRelation(ex, ey);
                        String c1 = (String) ex.get("eventCategory").getValue();
                        String c2 = (String) ey.get("eventCategory").getValue();

                        double rectDistance = new ObjectComparator().rectDistance(ex.get("lastObjectState") ,
                                ey.get("lastObjectState"));

                        if(relationType != null && rectDistance <= MIN_RECT_DISTANCE) {
                            Idea newCategoryIdea = cOEpisodeCategoryFactory.createCOEpisodeCategory(relationType, c1, c2, INIT_RELEVANCE);
                            COEpisodeCategory newCategory = (COEpisodeCategory) newCategoryIdea.getValue();

                            if(relationType.endsWith("i")) {
                                addRelation(ey, ex, newCategoryIdea.getName(), newCategory.getRelationType());
                                addRelation(ex, ey, newCategoryIdea.getName(), newCategory.getRelationType()+"i");
                            } else {
                                addRelation(ex, ey, newCategoryIdea.getName(), newCategory.getRelationType());
                                addRelation(ey, ex, newCategoryIdea.getName(), newCategory.getRelationType()+"i");
                            }
                        }
                    }
                }
            }
        }

        // add previousCOEpisodes that are not in the current timestamp;
        ArrayList<Integer> cOEpisodesIDs = getEpisodeIds(cOEpisodes);

        ArrayList<Idea> cOEpisodesNotPresentInCurrentsOEpisodes = filterEpisodesByIdsNotInList(
                previousCOEpisodes,
                cOEpisodesIDs);

//        TODO: uncomment the line below when creating the forgetting mechanism.
//        cOEpisodes.getL().addAll(cOEpisodesNotPresentInCurrentsOEpisodes);

        for(Idea cOEpisode : cOEpisodes.getL()) {
//            System.out.println(cOEpisode.toStringFull());
            System.out.println(cOEpisode.get("eventId").getValue() +": "+ cOEpisode.get("relations").getL().stream().map(relation->relation.get("eventId").getValue()).collect(Collectors.toList()));
        }

        return cOEpisodes;
    }


    public boolean verifyAndCreateRelationship(Idea ex, Idea ey, Idea categoryIdea) {
        String categoryName = categoryIdea.getName();
        COEpisodeCategory category = (COEpisodeCategory) categoryIdea.getValue();

        if (category.getRelevance() < RELEVANCE_THRESHOLD) {
            return false;
        }

        Idea membershipParameters = createMembershipParameters(ex, ey);
        double isMember = category.membership(membershipParameters);

        if(isMember==1) {
            addRelation(ex, ey, categoryName, category.getRelationType());
            addRelation(ey, ex, categoryName, category.getRelationType()+"i");
            return true;
        }
        return false;
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

    private boolean assignPreviousRelationsIfThereAre(Idea e1, Idea e2, Idea previousCOEpisodes) {
        Optional<Idea> pe1 = previousCOEpisodes.getL().stream()
                .filter(episode -> (int) episode.get("eventId").getValue() == (int) e1.get("eventId").getValue())
                .findFirst();

        Optional<Idea> pe2 = previousCOEpisodes.getL().stream()
                .filter(episode -> (int) episode.get("eventId").getValue() == (int) e2.get("eventId").getValue())
                .findFirst();

        if(pe1.isPresent() && pe2.isPresent()) {

            Optional<Idea> pe1Rpe2 = pe1.get().get("relations").getL().stream()
                    .filter(relation -> (int) relation.get("eventId").getValue() == (int) pe2.get().get("eventId").getValue()).findFirst();
            Optional<Idea> pe2Rpe1 = pe2.get().get("relations").getL().stream()
                    .filter(relation -> (int) relation.get("eventId").getValue() == (int) pe1.get().get("eventId").getValue()).findFirst();

            if(pe1Rpe2.isPresent() && pe2Rpe1.isPresent()) {
//                e1.get("relations").getL().add(pe1Rpe2.get().clone());
//                e2.get("relations").getL().add(pe2Rpe1.get().clone());
                return true;
            }
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

    private void initializeRelations(Idea episodes) {
        for(Idea episode : episodes.getL()) {
            if(episode.get("relations") == null) {
                Idea relations = new Idea("relations", "", 0);
                episode.add(relations);
            }
        }
    }

    private void duplicateRelations(Idea episodes, Idea previousEpisodes) {
        for(Idea episode : episodes.getL()) {
            int eventId = (int) episode.get("eventId").getValue();

            Optional<Idea> previousMatchingEpisode = previousEpisodes.getL().stream()
                    .filter(previousEpisode -> (int) previousEpisode.get("eventId").getValue()==eventId)
                    .findFirst();

            if(previousMatchingEpisode.isPresent()) {
                episode.get("relations").getL().addAll(previousMatchingEpisode.get().get("relations").getL());
            }
        }
    }

    public String identifyCOEpisodeCategoryRelation(Idea sOEpisode1, Idea sOEpisode2) {
        COEpisodeRelationIdentifier relationIdentifier = new COEpisodeRelationIdentifier();
        String relation = relationIdentifier.identifyRelationType(sOEpisode1, sOEpisode2);
        return relation;
    }
}
