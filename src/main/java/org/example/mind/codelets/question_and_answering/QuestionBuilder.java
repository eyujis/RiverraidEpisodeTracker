package org.example.mind.codelets.question_and_answering;

import br.unicamp.cst.representation.idea.Idea;

public class QuestionBuilder {

    public Idea build() {
        Idea questions = new Idea("questions", "", 0);
        questions.getL().add(buildHowManyQuestions());
        questions.getL().add(buildWhichObjectsDestroyedByMissiles());
        questions.getL().add(buildWhenSecondFuelQuestions());
        questions.getL().add(buildWhenBridgeTargetQuestions());

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

    private Idea buildWhenSecondFuelQuestions() {
        Idea whenSecondFuel = new Idea("whenSecondFuel", "", 0);

        String[] timestamps = {"moveStart", "moveEnd", "appeared", "disappeared"};

        for(String timestamp : timestamps) {
            whenSecondFuel.getL().add(new Idea(timestamp, ""));
        }

        return whenSecondFuel;
    }

    private Idea buildWhenBridgeTargetQuestions() {
        Idea whenSecondFuel = new Idea("whenBridgeTarget", "", 0);

        String[] timestamps = {"bridge_appear", "missile_launch", "explosion"};

        for(String timestamp : timestamps) {
            whenSecondFuel.getL().add(new Idea(timestamp, ""));
        }

        return whenSecondFuel;
    }
}
