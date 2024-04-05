package org.example.mind.codelets.perfect_episodic_memory;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class PerfectEpisodicStorageCodelet extends Codelet {
    Memory detectedCOEpisodesMO;
    Memory perfectEpisodicMO;

    @Override
    public void accessMemoryObjects() {
        detectedCOEpisodesMO=(MemoryObject)this.getInput("DETECTED_CO_EPISODES");
        perfectEpisodicMO=(MemoryObject)this.getInput("PERFECT_EPISODIC_MEMORY");
        perfectEpisodicMO=(MemoryObject)this.getOutput("PERFECT_EPISODIC_MEMORY");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        if(detectedCOEpisodesMO.getI()=="" || ((Idea) detectedCOEpisodesMO.getI()).getL().isEmpty()) {
            perfectEpisodicMO.setI(new Idea("episodicMemory", "", 0));
            return;
        }
        Idea detectedCOEpisodes = (Idea) detectedCOEpisodesMO.getI();
        int currentTimestamp = (int) detectedCOEpisodes.getValue();
        ArrayList<Idea> episodesToAdd = (ArrayList<Idea> ) detectedCOEpisodes.getL().stream()
                .filter(episode -> (boolean) episode.get("hasFinished").getValue()
                        // filter episodes that have finished in the previous frame
                        && (int) episode.get("currentTimestamp").getValue() == currentTimestamp-1)
                .collect(Collectors.toList());

        Idea perfectEpisodicMemory = (Idea) perfectEpisodicMO.getI();
        perfectEpisodicMemory.getL().addAll(episodesToAdd);
        perfectEpisodicMO.setI(perfectEpisodicMemory);
    }
}
