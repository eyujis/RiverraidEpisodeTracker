package org.example.mind.codelets.co_episode_cat_learner;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;

import java.util.stream.Collectors;

public class COEpisodeCategoryLearnerCodelet extends Codelet {
    Memory sOEpisodesMO;
    Memory cOEpisodeCategoriesMO;
    Memory sOEpisodesTSMO;
    COEpisodeCategoryLearner coEpisodeCategoryLearner = new COEpisodeCategoryLearner();

    @Override
    public void accessMemoryObjects() {
        sOEpisodesMO=(MemoryObject)this.getInput("DETECTED_EVENTS");
        cOEpisodeCategoriesMO=(MemoryObject)this.getOutput("CO_EPISODE_CATEGORIES");
        sOEpisodesTSMO=(MemoryObject)this.getOutput("DETECTED_SO_EPISODES_TS");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        if(sOEpisodesMO.getI()=="") {
            return;
        }

        if(sOEpisodesTSMO.getI()=="") {
            sOEpisodesTSMO.setI(-1);
        }

        // initialize cOEpisodeCategoriesMO
        if(cOEpisodeCategoriesMO.getI()=="") {
            cOEpisodeCategoriesMO.setI(new Idea("COEpisodeCategories", "", 0));
            return;
        }

        Idea sOEpisodes = (Idea) sOEpisodesMO.getI();
        Idea cOEpisodeCategories = (Idea) cOEpisodeCategoriesMO.getI();
        int lastTimestamp = (int) sOEpisodesTSMO.getI();

        synchronized (sOEpisodesMO) {
            synchronized (cOEpisodeCategoriesMO) {
                int currentTimestamp = (int) sOEpisodes.getValue();

                if(lastTimestamp==currentTimestamp) {
                    return;
                } else {
                    System.out.println(currentTimestamp);
                    sOEpisodesTSMO.setI(currentTimestamp);
                }

                Idea updatedCategories = coEpisodeCategoryLearner.updateCategories(sOEpisodes, cOEpisodeCategories);

//                System.out.println("===========================");
//                System.out.println(updatedCategories.getL().size());
//                System.out.println(updatedCategories.getL().stream()
//                        .filter(cat -> ((COEpisodeCategory)cat.getValue()).getRelevance()>=coEpisodeCategoryLearner.RELEVANCE_THRESHOLD)
//                        .collect(Collectors.toList()).size());

                cOEpisodeCategoriesMO.setI(updatedCategories);
            }
        }
    }
}
