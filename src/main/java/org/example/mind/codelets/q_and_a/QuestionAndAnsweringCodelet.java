package org.example.mind.codelets.q_and_a;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.RAWDataBufferizerCodelet;


public class QuestionAndAnsweringCodelet extends Codelet {
    Memory perfectEpisodicMO;
    Codelet rawDataBufferizerCodelet;
    boolean hasAnswered = false;

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
        if(perfectEpisodicMO.getI()=="") {
            return;
        }

        if(((RAWDataBufferizerCodelet)rawDataBufferizerCodelet).isSleep() && !hasAnswered){
            Idea perfectEpisodicMemory = (Idea) perfectEpisodicMO.getI();
            int numberOfHelicopters = (int) perfectEpisodicMemory.getL().stream()
                    .map(episode-> episode.get("lastObjectState"))
                    .filter(object->((String)object.get("objectLabel").getValue()).equals("jet"))
                    .map(object-> (int) object.get("id").getValue())
                    .distinct()
                    .count();
            System.out.println("Number of Jets: "+ numberOfHelicopters);
            hasAnswered = true;
        }
    }
}
