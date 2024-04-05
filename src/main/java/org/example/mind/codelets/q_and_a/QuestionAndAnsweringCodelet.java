package org.example.mind.codelets.q_and_a;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;


public class QuestionAndAnsweringCodelet extends Codelet {
    Memory perfectEpisodicMO;
    Codelet rawDataBufferizerCodelet;

    public QuestionAndAnsweringCodelet(Codelet rawDataBufferizerCodelet) {
        this.rawDataBufferizerCodelet = rawDataBufferizerCodelet;
    }

    @Override
    public void accessMemoryObjects() {
        perfectEpisodicMO=(MemoryObject)this.getInput("PERFECT_EPISODIC_MEMORY");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        if(!rawDataBufferizerCodelet.isLoop()) {
        }
    }
}
