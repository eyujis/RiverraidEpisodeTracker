package org.example.mind.codelets.co_episode_tracker;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;

public class COEpisodeTrackerCodelet extends Codelet {
    Memory sOEpisodesMO;
    Memory cOEpisodeCategoriesMO;
    Memory detectedCOEpisodesMO;

    @Override
    public void accessMemoryObjects() {
        sOEpisodesMO=(MemoryObject)this.getInput("DETECTED_EVENTS");
        cOEpisodeCategoriesMO=(MemoryObject)this.getInput("CO_EPISODE_CATEGORIES");
        detectedCOEpisodesMO=(MemoryObject)this.getInput("DETECTED_CO_EPISODES");

    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        if(sOEpisodesMO.getI()=="" || cOEpisodeCategoriesMO.getI()=="") {
            return;
        }

//        System.out.println(((Idea) sOEpisodesMO.getI()).toStringFull());

    }
}
