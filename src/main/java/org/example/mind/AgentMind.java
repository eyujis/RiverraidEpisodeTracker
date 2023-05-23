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
        Memory objectPCategoriesMO;
        Memory objectWCategoriesMO;


        createMemoryGroup("EpisodeTrackerMemoryGroup");
        createCodeletGroup("EpisodeTrackerCodeletGroup");

        rawDataBufferMO = createMemoryObject("RAW_DATA_BUFFER", "");
        detectedObjectsMO = createMemoryObject("DETECTED_OBJECTS", "");
        objectPCategoriesMO = createMemoryObject("OBJECT_PCATEGORIES", "");
        objectWCategoriesMO = createMemoryObject("OBJECT_WCATEGORIES", "");

        registerMemory(rawDataBufferMO, "EpisodeTrackerMemoryGroup");
        registerMemory(detectedObjectsMO, "EpisodeTrackerMemoryGroup");
        registerMemory(objectPCategoriesMO, "EpisodeTrackerMemoryGroup");
        registerMemory(objectWCategoriesMO, "EpisodeTrackerMemoryGroup");

        Codelet rawDataBufferizerCodelet = new RAWDataBufferizerCodelet(env, rawDataBufferImgJLabel);
        rawDataBufferizerCodelet.addOutput(rawDataBufferMO);
        rawDataBufferizerCodelet.setName("RAWDataBufferizer");
        insertCodelet(rawDataBufferizerCodelet);

        Codelet objectProposerCodelet = new ObjectProposerCodelet(objectsImgJLabel, mergedObjectsImgJLabel, categoriesImgJLabel);
        objectProposerCodelet.addInput(rawDataBufferMO);
        objectProposerCodelet.addInput(objectPCategoriesMO);
        objectProposerCodelet.addInput(objectWCategoriesMO);
        objectProposerCodelet.addOutput(detectedObjectsMO);
        objectProposerCodelet.setIsMemoryObserver(true);
        rawDataBufferMO.addMemoryObserver(objectProposerCodelet);
        objectProposerCodelet.setName("ObjectProposer");
        insertCodelet(objectProposerCodelet);

        Codelet objectCategoryLearnerCodelet = new ObjectCategoryLearnerCodelet();
        objectCategoryLearnerCodelet.addInput(detectedObjectsMO);
        objectCategoryLearnerCodelet.addOutput(objectPCategoriesMO);
        objectCategoryLearnerCodelet.addOutput(objectWCategoriesMO);
        objectCategoryLearnerCodelet.setIsMemoryObserver(true);
        detectedObjectsMO.addMemoryObserver(objectCategoryLearnerCodelet);
        objectCategoryLearnerCodelet.setName("ObjectCategoryLearner");
        insertCodelet(objectCategoryLearnerCodelet);

        registerCodelet(rawDataBufferizerCodelet, "EpisodeTrackerCodeletGroup");
        registerCodelet(objectProposerCodelet, "EpisodeTrackerCodeletGroup");
        registerCodelet(objectCategoryLearnerCodelet, "EpisodeTrackerCodeletGroup");

        // Sets a time step for running the codelets to avoid heating too much your machine
        for (Codelet c : this.getCodeRack().getAllCodelets())
            c.setTimeStep(50);

        // Start Cognitive Cycle
        start();

    }
}
