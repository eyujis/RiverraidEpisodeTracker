package org.example.mind;

import br.unicamp.cst.core.entities.*;
import org.example.environment.RiverRaidEnv;
import org.example.mind.codelets.ObjectProposerCodelet;
import org.example.mind.codelets.RAWDataBufferizerCodelet;

import javax.swing.*;
import java.io.IOException;


public class AgentMind extends Mind {
    public AgentMind(RiverRaidEnv env,
                     JLabel rawDataBufferImgJLabel,
                     JLabel objectsImgJLabel,
                     JLabel mergedObjectsImgJLabel) throws IOException {
        super();

        Memory rawDataBufferMO;
        Memory detectedObjectsMO;


        createMemoryGroup("EpisodeTrackerMemoryGroup");
        createCodeletGroup("EpisodeTrackerCodeletGroup");

        rawDataBufferMO = createMemoryObject("RAW_DATA_BUFFER", "");
        detectedObjectsMO = createMemoryObject("DETECTED_OBJECTS", "");

        registerMemory(rawDataBufferMO, "EpisodeTrackerMemoryGroup");
        registerMemory(detectedObjectsMO, "EpisodeTrackerMemoryGroup");

        Codelet rawDataBufferizerCodelet = new RAWDataBufferizerCodelet(env, rawDataBufferImgJLabel);
        rawDataBufferizerCodelet.addOutput(rawDataBufferMO);
        rawDataBufferizerCodelet.setName("RAWDataBufferizer");
        insertCodelet(rawDataBufferizerCodelet);

        Codelet objectProposerCodelet = new ObjectProposerCodelet(objectsImgJLabel, mergedObjectsImgJLabel);
        objectProposerCodelet.addInput(rawDataBufferMO);
        objectProposerCodelet.addOutput(detectedObjectsMO);
        objectProposerCodelet.setIsMemoryObserver(true);
        rawDataBufferMO.addMemoryObserver(objectProposerCodelet);
        objectProposerCodelet.setName("ObjectProposer");
        insertCodelet(objectProposerCodelet);

        registerCodelet(rawDataBufferizerCodelet, "EpisodeTrackerCodeletGroup");
        registerCodelet(objectProposerCodelet, "EpisodeTrackerCodeletGroup");


        // Sets a time step for running the codelets to avoid heating too much your machine
        for (Codelet c : this.getCodeRack().getAllCodelets())
            c.setTimeStep(50);

        // Start Cognitive Cycle
        start();

    }
}
