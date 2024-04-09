package org.example.mind.codelets.question_and_answering;

import br.unicamp.cst.representation.idea.Idea;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Answerer {

    public Idea answerQuestions(Idea questions, Idea episodes) {
        answerHowManyQuestions(questions.get("howMany"), episodes);
        answerWhichObjectsDestroyedByMissiles(questions.get("whichObjectsDestroyedByMissiles"), episodes);

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
                    whichObjectsDestroyedByMissiles.get(disappearedObjectLabel)
                            .setValue((int) whichObjectsDestroyedByMissiles.get(disappearedObjectLabel).getValue() + 1);
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
}
