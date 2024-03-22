package org.example.mind.codelets.co_episode_cat_learner;

import br.unicamp.cst.representation.idea.Category;
import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_proposer.ObjectComparator;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class COEpisodeCategory implements Category {
    String relationType;
    String sOEpisodeCategoryX;
    String sOEpisodeCategoryY;
    double relevance;
    double MIN_RECT_DISTANCE = 2;


    public COEpisodeCategory(String relationType, String sOEpisodeCategoryX, String sOEpisodeCategoryY, double relevance) {
        this.relationType = relationType;
        this.sOEpisodeCategoryX = sOEpisodeCategoryX;
        this.sOEpisodeCategoryY = sOEpisodeCategoryY;
        this.relevance = relevance;

        if(relationType.endsWith("i")) {
            Logger.getLogger(COEpisodeCategory.class.getName()).log(Level.SEVERE,
                    "got inverse name in the constructor");
        }
    }

    public boolean sameCategory(COEpisodeCategory c2) {
        if (this.relationType.equals(c2.getRelationType())
                && this.sOEpisodeCategoryX.equals(c2.getsOEpisodeCategoryX())
                && this.sOEpisodeCategoryY.equals(c2.getSOEpisodeCategoryY())) {
            return true;
        }
        return false;
    }

    @Override
    public Idea getInstance(List<Idea> constraints) {
        return null;
    }

    @Override
    public double membership(Idea idea) {
        Idea sOEpisodeX = idea.getL().get(0);
        Idea sOEpisodeY = idea.getL().get(1);

        double rectDistance = new ObjectComparator().rectDistance(sOEpisodeX.get("lastObjectState") ,
                sOEpisodeY.get("lastObjectState"));

        String sOEventCatX = (String) sOEpisodeX.get("eventCategory").getValue();
        String sOEventCatY = (String) sOEpisodeY.get("eventCategory").getValue();

        String rcvRelationType = new COEpisodeRelationIdentifier().identifyRelationType(sOEpisodeX, sOEpisodeY);

        if(rectDistance<=MIN_RECT_DISTANCE
                && sOEpisodeCategoryX.equals(sOEventCatX)
                && sOEpisodeCategoryY.equals(sOEventCatY)
                && relationType.equals(rcvRelationType)) {
            return 1;
        }

        return 0;
    }

    public double getRelevance() {
        return relevance;
    }

    public void decrementRelevance(double discount) {
        this.relevance -= discount;
    }

    public void incrementRelevance(double increment) {
        this.relevance += increment;
    }

    public String getRelationType() {
        return relationType;
    }

    public String getsOEpisodeCategoryX() {
        return sOEpisodeCategoryX;
    }

    public String getSOEpisodeCategoryY() {
        return sOEpisodeCategoryY;
    }
}
