package org.example.mind;

import br.unicamp.cst.core.entities.*;
import org.example.environment.RiverRaidEnv;
import org.example.mind.codelets.ActionCommunicatorCodelet;
import org.example.mind.codelets.co_episode_cat_learner.COEpisodeCategoryLearnerCodelet;
import org.example.mind.codelets.co_episode_tracker.COEpisodeTrackerCodelet;
import org.example.mind.codelets.event_cat_learner.EventCategoryLearnerCodelet;
import org.example.mind.codelets.event_tracker.EventTrackerCodelet;
import org.example.mind.codelets.forgetting_so_episodes.ForgettingSOEpisodesCodelet;
import org.example.mind.codelets.object_cat_learner.ObjectCategoryLearnerCodelet;
import org.example.mind.codelets.object_cat_learner.entities.ObjectCategory;
import org.example.mind.codelets.object_proposer.ObjectProposerCodelet;
import org.example.mind.codelets.RAWDataBufferizerCodelet;
import org.example.mind.codelets.objects_bufferizer.ObjectsBufferizerCodelet;
import org.example.visualization.FirstJFrame;
import org.example.visualization.SecondJFrame;

import javax.swing.*;
import java.io.IOException;


public class AgentMind extends Mind {
    JLabel rawDataBufferImgJLabel = null;
    JLabel objectsImgJLabel = null;
    JLabel mergedObjectsImgJLabel = null;
    JLabel categoriesImgJLabel = null;
    JLabel eventImgJLabel = null;
    JLabel forgettingSOEpisodeImgJLabel = null;

    public AgentMind(RiverRaidEnv env, FirstJFrame firstJFrame, SecondJFrame secondJFrame) throws IOException {
        super();

        if(firstJFrame!=null) {
             rawDataBufferImgJLabel = firstJFrame.getRawDataBufferImgJLabel();
             objectsImgJLabel = firstJFrame.getObjectsImgJLabel();
             mergedObjectsImgJLabel = firstJFrame.getMergedObjectsImgJLabel();
             categoriesImgJLabel = firstJFrame.getCategoriesImgJLabel();
        }
        if(secondJFrame!=null) {
            eventImgJLabel = secondJFrame.getEventTrackerImgJLabel();
            forgettingSOEpisodeImgJLabel = secondJFrame.getForgettingSOEpisodesImgJLabel();
        }

        Memory imageBuffer;
        Memory rewardBuffer;
        Memory terminalBuffer;
        Memory detectedFragmentsMO;
        Memory detectedObjectsMO;
        Memory fragmentCategoriesMO;
        Memory objectCategoriesMO;
        Memory objectsBufferMO;
        Memory eventCategoriesMO;
        Memory detectedEventsMO;
        Memory cOEpisodeCategoriesMO;
        Memory detectedCOEpisodesMO;
        Memory cOEpisodeCategoriesTSMO;
        Memory cOEpisodeTrackerTSMO;
        Memory actionTimestampMO;

        createMemoryGroup("EpisodeTrackerMemoryGroup");
        createCodeletGroup("EpisodeTrackerCodeletGroup");

        imageBuffer = createMemoryObject("IMAGE_BUFFER", "");
        rewardBuffer = createMemoryObject("REWARD_BUFFER", "");
        terminalBuffer = createMemoryObject("TERMINAL_BUFFER", "");
        detectedFragmentsMO = createMemoryObject("DETECTED_FRAGMENTS", "");
        detectedObjectsMO = createMemoryObject("DETECTED_OBJECTS", "");
        fragmentCategoriesMO = createMemoryObject("FRAGMENT_CATEGORIES", "");
        objectCategoriesMO = createMemoryObject("OBJECT_CATEGORIES", "");
        objectsBufferMO = createMemoryObject("OBJECTS_BUFFER", "");
        eventCategoriesMO = createMemoryObject("EVENT_CATEGORIES", "");
        detectedEventsMO = createMemoryObject("DETECTED_EVENTS", "");
        cOEpisodeCategoriesMO = createMemoryObject("CO_EPISODE_CATEGORIES", "");
        detectedCOEpisodesMO = createMemoryObject("DETECTED_CO_EPISODES", "");
        cOEpisodeCategoriesTSMO= createMemoryObject("CO_EPISODE_CATEGORIES_TS", "");
        cOEpisodeTrackerTSMO = createMemoryObject("CO_EPISODES_TS", "");
        actionTimestampMO = createMemoryObject("ACTION_TIMESTAMP", 0);

        registerMemory(imageBuffer, "EpisodeTrackerMemoryGroup");
        registerMemory(rewardBuffer, "EpisodeTrackerMemoryGroup");
        registerMemory(terminalBuffer, "EpisodeTrackerMemoryGroup");
        registerMemory(detectedFragmentsMO, "EpisodeTrackerMemoryGroup");
        registerMemory(detectedObjectsMO, "EpisodeTrackerMemoryGroup");
        registerMemory(fragmentCategoriesMO, "EpisodeTrackerMemoryGroup");
        registerMemory(objectCategoriesMO, "EpisodeTrackerMemoryGroup");
        registerMemory(objectsBufferMO, "EpisodeTrackerMemoryGroup");
        registerMemory(eventCategoriesMO, "EpisodeTrackerMemoryGroup");
        registerMemory(detectedEventsMO, "EpisodeTrackerMemoryGroup");
        registerMemory(cOEpisodeCategoriesMO, "EpisodeTrackerMemoryGroup");
        registerMemory(cOEpisodeCategoriesTSMO, "EpisodeTrackerMemoryGroup");
        registerMemory(cOEpisodeTrackerTSMO, "EpisodeTrackerMemoryGroup");

        Codelet rawDataBufferizerCodelet = new RAWDataBufferizerCodelet(env, rawDataBufferImgJLabel);
        rawDataBufferizerCodelet.addInput(actionTimestampMO);
        rawDataBufferizerCodelet.addOutput(imageBuffer);
        rawDataBufferizerCodelet.addOutput(rewardBuffer);
        rawDataBufferizerCodelet.addOutput(terminalBuffer);
        rawDataBufferizerCodelet.setName("RAWDataBufferizer");
        insertCodelet(rawDataBufferizerCodelet);

        Codelet objectProposerCodelet = new ObjectProposerCodelet(objectsImgJLabel, mergedObjectsImgJLabel, categoriesImgJLabel);
        objectProposerCodelet.addInput(imageBuffer);
        objectProposerCodelet.addInput(fragmentCategoriesMO);
        objectProposerCodelet.addOutput(fragmentCategoriesMO);
        objectProposerCodelet.addInput(objectCategoriesMO);
        objectProposerCodelet.addOutput(objectCategoriesMO);
        objectProposerCodelet.addOutput(detectedFragmentsMO);
        objectProposerCodelet.addOutput(detectedObjectsMO);
        objectProposerCodelet.setIsMemoryObserver(true);
        imageBuffer.addMemoryObserver(objectProposerCodelet);
        objectProposerCodelet.setName("ObjectProposer");
        insertCodelet(objectProposerCodelet);

        Codelet objectCategoryLearnerCodelet = new ObjectCategoryLearnerCodelet();
        objectCategoryLearnerCodelet.addInput(detectedFragmentsMO);
        objectCategoryLearnerCodelet.addOutput(fragmentCategoriesMO);
        objectCategoryLearnerCodelet.addOutput(objectCategoriesMO);
        objectCategoryLearnerCodelet.setIsMemoryObserver(true);
        detectedFragmentsMO.addMemoryObserver(objectCategoryLearnerCodelet);
        objectCategoryLearnerCodelet.setName("ObjectCategoryLearner");
        insertCodelet(objectCategoryLearnerCodelet);

        Codelet objectsBufferizerCodelet = new ObjectsBufferizerCodelet();
        objectsBufferizerCodelet.addInput(detectedObjectsMO);
        objectsBufferizerCodelet.addOutput(objectsBufferMO);
        objectsBufferizerCodelet.setIsMemoryObserver(true);
        detectedObjectsMO.addMemoryObserver(objectsBufferizerCodelet);
        objectsBufferizerCodelet.setName("ObjectsBufferizer");
        insertCodelet(objectsBufferizerCodelet);

        Codelet eventCategoryLearnerCodelet = new EventCategoryLearnerCodelet();
        eventCategoryLearnerCodelet.addInput(objectsBufferMO);
        eventCategoryLearnerCodelet.addOutput(eventCategoriesMO);
        eventCategoryLearnerCodelet.setIsMemoryObserver(true);
        objectsBufferMO.addMemoryObserver(eventCategoryLearnerCodelet);
        eventCategoryLearnerCodelet.setName("EventCategoryLearner");
        insertCodelet(eventCategoryLearnerCodelet);

        Codelet actionCommunicatorCodelet = new ActionCommunicatorCodelet(env);

        Codelet eventTrackerCodelet = new EventTrackerCodelet(eventImgJLabel, actionCommunicatorCodelet);
        eventTrackerCodelet.addInput(objectsBufferMO);
        eventTrackerCodelet.addInput(eventCategoriesMO);
        eventTrackerCodelet.addOutput(eventCategoriesMO);
        eventTrackerCodelet.addOutput(detectedEventsMO);
        eventTrackerCodelet.setIsMemoryObserver(true);
        objectsBufferMO.addMemoryObserver(eventTrackerCodelet);
        eventTrackerCodelet.setName("EventTracker");
        insertCodelet(eventTrackerCodelet);

        Codelet forgettingSOEpisodesCodelet = new ForgettingSOEpisodesCodelet(forgettingSOEpisodeImgJLabel);
        forgettingSOEpisodesCodelet.addInput(detectedEventsMO);
        forgettingSOEpisodesCodelet.addOutput(detectedEventsMO);
        forgettingSOEpisodesCodelet.setName("ForgettingSOEpisodes");
        insertCodelet(forgettingSOEpisodesCodelet);

        actionCommunicatorCodelet.addOutput(actionTimestampMO);
        actionCommunicatorCodelet.setIsMemoryObserver(true); // TODO: Complete RL Logic
        insertCodelet(actionCommunicatorCodelet);

        registerCodelet(rawDataBufferizerCodelet, "EpisodeTrackerCodeletGroup");
        registerCodelet(objectProposerCodelet, "EpisodeTrackerCodeletGroup");
        registerCodelet(objectCategoryLearnerCodelet, "EpisodeTrackerCodeletGroup");
        registerCodelet(objectsBufferizerCodelet, "EpisodeTrackerCodeletGroup");
//        registerCodelet(eventCategoryLearnerCodelet, "EpisodeTrackerCodeletGroup");
//        registerCodelet(eventTrackerCodelet, "EpisodeTrackerCodeletGroup");
//        registerCodelet(forgettingSOEpisodesCodelet, "EpisodeTrackerCodeletGroup");
//        registerCodelet(cOEpisodeCategoryLearnerCodelet, "EpisodeTrackerCodeletGroup");
//        registerCodelet(cOEpisodeTrackerCodelet, "EpisodeTrackerCodeletGroup");

        // Sets a time step for running the codelets to avoid heating too much your machine
        for (Codelet c : this.getCodeRack().getAllCodelets())
            c.setTimeStep(1);

        // Start Cognitive Cycle
        start();
    }
}
