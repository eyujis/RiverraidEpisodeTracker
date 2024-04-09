package org.example.mind.codelets.question_and_answering;

import br.unicamp.cst.representation.idea.Idea;

public class QuestionBuilder {

    public Idea build() {
        Idea questions = new Idea("questions", "", 0);
        questions.getL().add(buildHowManyQuestions());
        questions.getL().add(buildWhichObjectsDestroyedByMissiles());

        return questions;
    }

    private Idea buildWhichObjectsDestroyedByMissiles() {
        Idea whichObjectsDestroyedByMissiles = new Idea("whichObjectsDestroyedByMissiles", "", 0);

        String[] objectTypes = {"helicopter", "tanker", "fuel", "jet", "tree", "house", "bridge", "missile", "ship"};

        for(String objectType : objectTypes) {
            whichObjectsDestroyedByMissiles.getL().add(new Idea(objectType, 0));
        }

        return whichObjectsDestroyedByMissiles;
    }

    private Idea buildHowManyQuestions() {
        Idea howMany = new Idea("howMany", "", 0);

        String[] objectTypes = {"helicopter", "tanker", "fuel", "jet", "tree", "house", "bridge", "missile", "ship"};

        for(String objectType : objectTypes) {
            howMany.getL().add(new Idea(objectType, ""));
        }

        return howMany;
    }
}
