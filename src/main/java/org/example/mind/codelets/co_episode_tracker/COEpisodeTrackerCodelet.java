package org.example.mind.codelets.co_episode_tracker;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;

public class COEpisodeTrackerCodelet extends Codelet {
    Memory sOEpisodesMO;
    Memory cOEpisodeCategoriesMO;
    Memory detectedCOEpisodesMO;
    Memory cOEpisodeTrackerTSMO;

    @Override
    public void accessMemoryObjects() {
        sOEpisodesMO=(MemoryObject)this.getInput("DETECTED_EVENTS");
        cOEpisodeCategoriesMO=(MemoryObject)this.getInput("CO_EPISODE_CATEGORIES");
        detectedCOEpisodesMO=(MemoryObject)this.getOutput("DETECTED_CO_EPISODES");
        cOEpisodeTrackerTSMO =(MemoryObject)this.getOutput("CO_EPISODES_TS");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        if(sOEpisodesMO.getI()=="" || cOEpisodeCategoriesMO.getI()=="") {
            return;
        }

        if(detectedCOEpisodesMO.getI() == "") {
            detectedCOEpisodesMO.setI(new Idea("COEpisodes", "", 0));
            return;
        }

        if(cOEpisodeTrackerTSMO.getI()=="") {
            cOEpisodeTrackerTSMO.setI(-1);
        }

        Idea sOEpisodes = (Idea) sOEpisodesMO.getI();
        Idea cOEpisodeCategories = (Idea) cOEpisodeCategoriesMO.getI();
        Idea previousCOEpisodes = (Idea) detectedCOEpisodesMO.getI();

        int lastTimestamp = (int) cOEpisodeTrackerTSMO.getI();

        synchronized (sOEpisodesMO) {
            synchronized (cOEpisodeCategoriesMO) {
                synchronized (detectedCOEpisodesMO) {
                    int currentTimestamp = (int) sOEpisodes.getValue();

                    if(lastTimestamp==currentTimestamp) {
                        return;
                    } else {
                        cOEpisodeTrackerTSMO.setI(currentTimestamp);
                    }

                    Idea cOEpisodes = new COEpisodeTracker().updateRelations(sOEpisodes,
                            cOEpisodeCategories,
                            previousCOEpisodes);

                    detectedCOEpisodesMO.setI(cOEpisodes);
                }
            }
        }

    }
}
