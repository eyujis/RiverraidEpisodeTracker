package org.example.mind.codelets.co_episode_cat_learner;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;

import java.util.stream.Collectors;

public class COEpisodeCategoryLearnerCodelet extends Codelet {
    Memory sOEpisodesMO;
    Memory cOEpisodeCategoriesMO;
    COEpisodeCategoryLearner coEpisodeCategoryLearner = new COEpisodeCategoryLearner();

    @Override
    public void accessMemoryObjects() {
        sOEpisodesMO=(MemoryObject)this.getInput("DETECTED_EVENTS");
        cOEpisodeCategoriesMO=(MemoryObject)this.getOutput("CO_EPISODE_CATEGORIES");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        if(sOEpisodesMO.getI()=="") {
            return;
        }
        // initialize cOEpisodeCategoriesMO
        if(cOEpisodeCategoriesMO.getI()=="") {
            cOEpisodeCategoriesMO.setI(new Idea("COEpisodeCategories", "", 0));
            return;
        }

        Idea sOEpisodes = (Idea) sOEpisodesMO.getI();
        Idea cOEpisodeCategories = (Idea) cOEpisodeCategoriesMO.getI();

        synchronized (sOEpisodesMO) {
            synchronized (cOEpisodeCategoriesMO) {
                Idea updatedCategories = coEpisodeCategoryLearner.updateCategories(sOEpisodes, cOEpisodeCategories);

                System.out.println("===========================");
                System.out.println(updatedCategories.getL().size());
                System.out.println(updatedCategories.getL().stream()
                        .filter(cat -> ((COEpisodeCategory)cat.getValue()).getRelevance()>=coEpisodeCategoryLearner.RELEVANCE_THRESHOLD).collect(Collectors.toList()).size());

                cOEpisodeCategoriesMO.setI(updatedCategories);
            }
        }
    }
}
