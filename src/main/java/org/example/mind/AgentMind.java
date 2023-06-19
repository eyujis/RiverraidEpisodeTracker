package org.example.mind;

import br.unicamp.cst.core.entities.*;
import org.example.environment.RiverRaidEnv;
import org.example.mind.codelets.object_cat_learner.ObjectCategoryLearnerCodelet;
import org.example.mind.codelets.object_proposer_codelet.ObjectProposerCodelet;
import org.example.mind.codelets.RAWDataBufferizerCodelet;

import javax.swing.*;
import java.io.IOException;


public class AgentMind extends Mind {
    public AgentMind(RiverRaidEnv env,
                     JLabel rawDataBufferImgJLabel,
                     JLabel objectsImgJLabel,
                     JLabel mergedObjectsImgJLabel,
                     JLabel categoriesImgJLabel) throws IOException {
        super();

        Memory rawDataBufferMO;
        Memory detectedObjectsMO;
        Memory FragmentCategoriesMO;
        Memory objectCategoriesMO;


        createMemoryGroup("EpisodeTrackerMemoryGroup");
        createCodeletGroup("EpisodeTrackerCodeletGroup");

        rawDataBufferMO = createMemoryObject("RAW_DATA_BUFFER", "");
        detectedObjectsMO = createMemoryObject("DETECTED_OBJECTS", "");
        FragmentCategoriesMO = createMemoryObject("FRAGMENT_CATEGORIES", "");
        objectCategoriesMO = createMemoryObject("OBJECT_CATEGORIES", "");

        registerMemory(rawDataBufferMO, "EpisodeTrackerMemoryGroup");
        registerMemory(detectedObjectsMO, "EpisodeTrackerMemoryGroup");
        registerMemory(FragmentCategoriesMO, "EpisodeTrackerMemoryGroup");
        registerMemory(objectCategoriesMO, "EpisodeTrackerMemoryGroup");

        Codelet rawDataBufferizerCodelet = new RAWDataBufferizerCodelet(env, rawDataBufferImgJLabel);
        rawDataBufferizerCodelet.addOutput(rawDataBufferMO);
        rawDataBufferizerCodelet.setName("RAWDataBufferizer");
        insertCodelet(rawDataBufferizerCodelet);

        Codelet objectProposerCodelet = new ObjectProposerCodelet(objectsImgJLabel, mergedObjectsImgJLabel, categoriesImgJLabel);
        objectProposerCodelet.addInput(rawDataBufferMO);
        objectProposerCodelet.addInput(FragmentCategoriesMO);
        objectProposerCodelet.addInput(objectCategoriesMO);
        objectProposerCodelet.addOutput(detectedObjectsMO);
        objectProposerCodelet.setIsMemoryObserver(true);
        rawDataBufferMO.addMemoryObserver(objectProposerCodelet);
        objectProposerCodelet.setName("ObjectProposer");
        insertCodelet(objectProposerCodelet);

        Codelet objectCategoryLearnerCodelet = new ObjectCategoryLearnerCodelet();
        objectCategoryLearnerCodelet.addInput(detectedObjectsMO);
        objectCategoryLearnerCodelet.addOutput(FragmentCategoriesMO);
        objectCategoryLearnerCodelet.addOutput(objectCategoriesMO);
        objectCategoryLearnerCodelet.setIsMemoryObserver(true);
        detectedObjectsMO.addMemoryObserver(objectCategoryLearnerCodelet);
        objectCategoryLearnerCodelet.setName("ObjectCategoryLearner");
        insertCodelet(objectCategoryLearnerCodelet);

        registerCodelet(rawDataBufferizerCodelet, "EpisodeTrackerCodeletGroup");
        registerCodelet(objectProposerCodelet, "EpisodeTrackerCodeletGroup");
        registerCodelet(objectCategoryLearnerCodelet, "EpisodeTrackerCodeletGroup");

        // Sets a time step for running the codelets to avoid heating too much your machine
        for (Codelet c : this.getCodeRack().getAllCodelets())
            c.setTimeStep(150);

        // Start Cognitive Cycle
        start();

    }
}
