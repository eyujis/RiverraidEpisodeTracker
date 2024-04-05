package org.example.mind;

import br.unicamp.cst.core.entities.*;
import org.example.environment.RiverRaidEnv;
import org.example.mind.codelets.co_episode_cat_learner.COEpisodeCategoryLearnerCodelet;
import org.example.mind.codelets.co_episode_tracker.COEpisodeTrackerCodelet;
import org.example.mind.codelets.event_cat_learner.EventCategoryLearnerCodelet;
import org.example.mind.codelets.event_tracker.EventTrackerCodelet;
import org.example.mind.codelets.forgetting_so_episodes.ForgettingSOEpisodesCodelet;
import org.example.mind.codelets.object_cat_learner.ObjectCategoryLearnerCodelet;
import org.example.mind.codelets.object_proposer.ObjectProposerCodelet;
import org.example.mind.codelets.RAWDataBufferizerCodelet;
import org.example.mind.codelets.objects_bufferizer.ObjectsBufferizerCodelet;
import org.example.mind.codelets.perfect_episodic_memory.PerfectEpisodicStorageCodelet;
import org.example.mind.codelets.q_and_a.QuestionAndAnsweringCodelet;
import org.example.visualization.FirstJFrame;
import org.example.visualization.SecondJFrame;

import javax.swing.*;
import java.io.IOException;


public class AgentMind extends Mind {
    public AgentMind(RiverRaidEnv env, FirstJFrame firstJFrame, SecondJFrame secondJFrame) throws IOException {
        super();

        JLabel rawDataBufferImgJLabel = firstJFrame.getRawDataBufferImgJLabel();
        JLabel objectsImgJLabel = firstJFrame.getObjectsImgJLabel();
        JLabel mergedObjectsImgJLabel = firstJFrame.getMergedObjectsImgJLabel();
        JLabel categoriesImgJLabel = firstJFrame.getCategoriesImgJLabel();

        JLabel eventImgJLabel = secondJFrame.getEventTrackerImgJLabel();
        JLabel forgettingSOEpisodeImgJLabel = secondJFrame.getForgettingSOEpisodesImgJLabel();

        JLabel cOEpisodesImgJLabel = secondJFrame.getcOEpisodesImgJLabel();

        Memory rawDataBufferMO;
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
        Memory perfectEpisodicMO;

        createMemoryGroup("EpisodeTrackerMemoryGroup");
        createCodeletGroup("EpisodeTrackerCodeletGroup");

        rawDataBufferMO = createMemoryObject("RAW_DATA_BUFFER", "");
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
        perfectEpisodicMO = createMemoryObject("PERFECT_EPISODIC_MEMORY", "");

        registerMemory(rawDataBufferMO, "EpisodeTrackerMemoryGroup");
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
        registerMemory(perfectEpisodicMO, "EpisodeTrackerMemoryGroup");

        Codelet rawDataBufferizerCodelet = new RAWDataBufferizerCodelet(env, rawDataBufferImgJLabel);
        rawDataBufferizerCodelet.addOutput(rawDataBufferMO);
        rawDataBufferizerCodelet.setName("RAWDataBufferizer");
        insertCodelet(rawDataBufferizerCodelet);

        Codelet objectProposerCodelet = new ObjectProposerCodelet(objectsImgJLabel, mergedObjectsImgJLabel, categoriesImgJLabel);
        objectProposerCodelet.addInput(rawDataBufferMO);
        objectProposerCodelet.addInput(fragmentCategoriesMO);
        objectProposerCodelet.addOutput(fragmentCategoriesMO);
        objectProposerCodelet.addInput(objectCategoriesMO);
        objectProposerCodelet.addOutput(objectCategoriesMO);
        objectProposerCodelet.addOutput(detectedFragmentsMO);
        objectProposerCodelet.addOutput(detectedObjectsMO);
        objectProposerCodelet.setIsMemoryObserver(true);
        rawDataBufferMO.addMemoryObserver(objectProposerCodelet);
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

        Codelet eventTrackerCodelet = new EventTrackerCodelet(eventImgJLabel);
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

        Codelet cOEpisodeCategoryLearnerCodelet = new COEpisodeCategoryLearnerCodelet();
        cOEpisodeCategoryLearnerCodelet.addInput(detectedEventsMO);
        cOEpisodeCategoryLearnerCodelet.addInput(cOEpisodeCategoriesMO);
        cOEpisodeCategoryLearnerCodelet.addOutput(cOEpisodeCategoriesTSMO);
        cOEpisodeCategoryLearnerCodelet.addOutput(cOEpisodeCategoriesMO);
        cOEpisodeCategoryLearnerCodelet.addOutput(cOEpisodeCategoriesTSMO);
        cOEpisodeCategoryLearnerCodelet.setIsMemoryObserver(true);
        detectedEventsMO.addMemoryObserver(cOEpisodeCategoryLearnerCodelet);
        cOEpisodeCategoryLearnerCodelet.setName("COEpisodeCategoryLearner");
        insertCodelet(cOEpisodeCategoryLearnerCodelet);

        Codelet cOEpisodeTrackerCodelet = new COEpisodeTrackerCodelet(cOEpisodesImgJLabel);
        cOEpisodeTrackerCodelet.addInput(detectedEventsMO);
        cOEpisodeTrackerCodelet.addInput(cOEpisodeCategoriesMO);
        cOEpisodeTrackerCodelet.addOutput(cOEpisodeCategoriesMO);
        cOEpisodeTrackerCodelet.addInput(cOEpisodeTrackerTSMO);
        cOEpisodeTrackerCodelet.addOutput(cOEpisodeTrackerTSMO);
        cOEpisodeTrackerCodelet.addOutput(detectedCOEpisodesMO);
        cOEpisodeTrackerCodelet.setIsMemoryObserver(true);
        detectedEventsMO.addMemoryObserver(cOEpisodeTrackerCodelet);
        cOEpisodeTrackerCodelet.setName("COEpisodeTracker");
        insertCodelet(cOEpisodeTrackerCodelet);

        Codelet perfectEpisodeStorageCodelet = new PerfectEpisodicStorageCodelet();
        perfectEpisodeStorageCodelet.addInput(detectedCOEpisodesMO);
        perfectEpisodeStorageCodelet.addInput(perfectEpisodicMO);
        perfectEpisodeStorageCodelet.addOutput(perfectEpisodicMO);
        perfectEpisodeStorageCodelet.setIsMemoryObserver(true);
        detectedCOEpisodesMO.addMemoryObserver(perfectEpisodeStorageCodelet);
        perfectEpisodeStorageCodelet.setName("PerfectEpisodicStorage");
        insertCodelet(perfectEpisodeStorageCodelet);

        Codelet questionAndAnsweringCodelet = new QuestionAndAnsweringCodelet(rawDataBufferizerCodelet);
        questionAndAnsweringCodelet.addInput(perfectEpisodicMO);
        questionAndAnsweringCodelet.setName("QuestionAndAnswering");
        insertCodelet(questionAndAnsweringCodelet);

        registerCodelet(rawDataBufferizerCodelet, "EpisodeTrackerCodeletGroup");
        registerCodelet(objectProposerCodelet, "EpisodeTrackerCodeletGroup");
        registerCodelet(objectCategoryLearnerCodelet, "EpisodeTrackerCodeletGroup");
        registerCodelet(objectsBufferizerCodelet, "EpisodeTrackerCodeletGroup");
        registerCodelet(eventCategoryLearnerCodelet, "EpisodeTrackerCodeletGroup");
        registerCodelet(eventTrackerCodelet, "EpisodeTrackerCodeletGroup");
        registerCodelet(forgettingSOEpisodesCodelet, "EpisodeTrackerCodeletGroup");
        registerCodelet(cOEpisodeCategoryLearnerCodelet, "EpisodeTrackerCodeletGroup");
        registerCodelet(cOEpisodeTrackerCodelet, "EpisodeTrackerCodeletGroup");
        registerCodelet(perfectEpisodeStorageCodelet, "EpisodeTrackerCodeletGroup");
        registerCodelet(questionAndAnsweringCodelet, "EpisodeTrackerCodeletGroup");

        // Sets a time step for running the codelets to avoid heating too much your machine
        for (Codelet c : this.getCodeRack().getAllCodelets())
            c.setTimeStep(100);

        // Start Cognitive Cycle
        start();

    }
}
