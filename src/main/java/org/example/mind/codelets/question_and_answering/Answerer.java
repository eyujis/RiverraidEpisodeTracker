package org.example.mind.codelets.question_and_answering;

import br.unicamp.cst.representation.idea.Idea;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Answerer {

    public Idea answerQuestions(Idea questions, Idea episodes) {
        answerHowManyQuestions(questions.get("howMany"), episodes);
        answerWhichObjectsDestroyedByMissiles(questions.get("whichObjectsDestroyedByMissiles"), episodes);
        answerWhenSecondFuel(questions.get("whenSecondFuel"), episodes);
        answerWhenBridgeTarget(questions.get("whenBridgeTarget"), episodes);

        return questions;
    }

    private void answerWhichObjectsDestroyedByMissiles(Idea whichObjectsDestroyedByMissiles, Idea episodes) {

        ArrayList<Idea> disappearEpisodes = (ArrayList<Idea>) episodes.getL().stream()
                .filter(episode -> ((String) episode.get("eventCategory").getValue()).startsWith("AppearanceEventCategory"))
                .filter(episode -> ((String) episode.get("appearanceEventType").getValue()).startsWith("disappear"))
                .collect(Collectors.toList());

        for(Idea disappearEpisode : disappearEpisodes) {
            ArrayList<Integer> causeEpisodeIds = (ArrayList<Integer>) disappearEpisode.get("relations").getL().stream()
                    .filter(relation -> ((String) relation.get("relationType").getValue()).equals("mi"))
                    .map(relation -> (int) relation.get("eventId").getValue())
                    .collect(Collectors.toList());

            int disappearedObjectId = (int) disappearEpisode.get("objectId").getValue();

            for(Integer causeEpisodeId : causeEpisodeIds) {
                Idea causeEpisode = getEventById(causeEpisodeId, episodes);
                int causeObjectId = (int) causeEpisode.get("objectId").getValue();

                if(disappearedObjectId!=causeObjectId
                        && ((String) causeEpisode.get("lastObjectState.objectLabel").getValue()).equals("missile")) {

                    String disappearedObjectLabel = (String) disappearEpisode.get("lastObjectState.objectLabel").getValue();

                    if(disappearedObjectLabel!="null" && disappearedObjectLabel!=null) {
                        whichObjectsDestroyedByMissiles.get(disappearedObjectLabel)
                                .setValue((int) whichObjectsDestroyedByMissiles.get(disappearedObjectLabel).getValue() + 1);
                    }
                }
            }
        }
    }

    private Idea getEventById(int eventId, Idea episodes) {
        ArrayList<Idea> episodesWithId = (ArrayList<Idea>) episodes.getL().stream()
                .filter(episode -> (int) episode.get("eventId").getValue() == eventId)
                .collect(Collectors.toList());

        if(episodesWithId.size()>1) {
            Logger.getLogger(Answerer.class.getName()).log(Level.SEVERE,
                    "More than two events matching the same event id.");
        } else if(episodesWithId.size()==0) {
            Logger.getLogger(Answerer.class.getName()).log(Level.SEVERE,
                    "No events matching the event id.");
        }

        return episodesWithId.get(0);
    }

    private void answerHowManyQuestions(Idea howManyQuestions, Idea episodes) {
        for(Idea question: howManyQuestions.getL()) {
            int nTypeObjects = answerHowManyObjectsWithType(question.getName(), episodes);
            question.setValue(nTypeObjects);
        }
    }

    private int answerHowManyObjectsWithType(String objectType, Idea episodes) {
        int nTypeObjects = (int) episodes.getL().stream()
                .map(episode-> episode.get("lastObjectState"))
                .filter(object->((String)object.get("objectLabel").getValue()).equals(objectType))
                .map(object-> (int) object.get("id").getValue())
                .distinct()
                .count();
        return nTypeObjects;
    }

    private void answerWhenSecondFuel(Idea whenSecondFuel, Idea episodes) {
        int objectId = getIthObjectTypeInstanceId(2, "fuel", episodes);
        int moveStart = getMovementTimestampWithObjectId(objectId, episodes, true);
        int moveEnd = getMovementTimestampWithObjectId(objectId, episodes, false);
        int appearTimestamp = getAppearanceTimestampWithObjectId(objectId, episodes, false);
        int disappearTimestamp = getAppearanceTimestampWithObjectId(objectId, episodes, true);

        whenSecondFuel.get("moveStart").setValue(moveStart);
        whenSecondFuel.get("moveEnd").setValue(moveEnd);
        whenSecondFuel.get("appeared").setValue(appearTimestamp);
        whenSecondFuel.get("disappeared").setValue(disappearTimestamp);
    }

    private int getMovementTimestampWithObjectId(int objectId, Idea episodes, boolean isInitial) {
        String timestampType = isInitial ? "initialTimestamp" : "currentTimestamp";

        Stream<Integer> timestamps = episodes.getL().stream()
                .filter(episode-> (int) episode.get("objectId").getValue()==objectId)
                .filter(episode-> ((String)episode.get("eventCategory").getValue()).startsWith("Vector"))
                .filter(episode -> !isZeroMagnitude((double[]) episode.get("categoryVector").getValue()))
                .map(episode-> (int) episode.get(timestampType).getValue());

        if(isInitial) {
            // +1 because the object is static in the initial timestamp
            return timestamps.min(Integer::compareTo).get() + 1;
        } else {
            return timestamps.max(Integer::compareTo).get();
        }

    }

    private int getAppearanceTimestampWithObjectId(int objectId, Idea episodes, boolean disappear) {
        String appearanceEventType = disappear ? "disappear" : "appear";

        Optional<Idea> appearanceEpisode = episodes.getL().stream()
                .filter(episode-> (int) episode.get("objectId").getValue()==objectId)
                .filter(episode-> ((String)episode.get("eventCategory").getValue()).startsWith("Appearance"))
                .filter(episode-> ((String)episode.get("appearanceEventType").getValue()).equals(appearanceEventType))
                .findFirst();

        if(appearanceEpisode.isPresent()) {
            return (int) appearanceEpisode.get().get("currentTimestamp").getValue();
        }
        return -1;
    }

    private int getIthObjectTypeInstanceId(int ith, String objectType, Idea episodes) {
        int objectId = episodes.getL().stream()
                .map(episode-> episode.get("lastObjectState"))
                .filter(object->((String)object.get("objectLabel").getValue()).equals(objectType))
                .map(object-> (int) object.get("id").getValue())
                .distinct()
                .sorted()
                .collect(Collectors.toList())
                .get(ith-1);
        return objectId;
    }

    private boolean isZeroMagnitude(double[] vector) {
        for(int i=0; i<vector.length; i++) {
            if(vector[i]!=0) {
                return false;
            }
        }
        return true;
    }

    private void answerWhenBridgeTarget(Idea whenBridgeTarget, Idea episodes) {
        int firstBridgeId = getIthObjectTypeInstanceId(1, "bridge", episodes);

        int bridgeAppearTimestamp = -1;
        int missileLaunchTimestamp = -1;
        int bridgeDisappearTimestamp = -1;

        Optional<Idea> disappearEpisode = episodes.getL().stream()
                .filter(episode-> (int) episode.get("objectId").getValue()==firstBridgeId)
                .filter(episode-> ((String)episode.get("eventCategory").getValue()).startsWith("Appearance"))
                .filter(episode-> ((String)episode.get("appearanceEventType").getValue()).equals("disappear"))
                .findFirst();

        if(disappearEpisode.isPresent()) {
            ArrayList<Integer> causeEpisodeIds = (ArrayList<Integer>) disappearEpisode.get().get("relations").getL().stream()
                    .filter(relation -> ((String) relation.get("relationType").getValue()).equals("mi"))
                    .map(relation -> (int) relation.get("eventId").getValue())
                    .collect(Collectors.toList());

            int disappearedObjectId = (int) disappearEpisode.get().get("objectId").getValue();

            for(Integer causeEpisodeId : causeEpisodeIds) {
                Idea causeEpisode = getEventById(causeEpisodeId, episodes);
                int causeObjectId = (int) causeEpisode.get("objectId").getValue();

                if(disappearedObjectId!=causeObjectId
                        && ((String) causeEpisode.get("lastObjectState.objectLabel").getValue()).equals("missile")) {

                    bridgeAppearTimestamp = getAppearanceTimestampWithObjectId(firstBridgeId, episodes, false);
                    missileLaunchTimestamp = getAppearanceTimestampWithObjectId(causeObjectId, episodes, false);
                    bridgeDisappearTimestamp = getAppearanceTimestampWithObjectId(firstBridgeId, episodes, true);
                }
            }
        }

        whenBridgeTarget.get("bridge_appear").setValue(bridgeAppearTimestamp);
        whenBridgeTarget.get("missile_launch").setValue(missileLaunchTimestamp);
        whenBridgeTarget.get("explosion").setValue(bridgeDisappearTimestamp);
    }
}
