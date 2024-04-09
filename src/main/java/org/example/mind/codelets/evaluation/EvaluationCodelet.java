package org.example.mind.codelets.evaluation;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.evaluation.trueValues.TrueValue0;
import org.example.mind.codelets.question_and_answering.QuestionBuilder;

import java.util.HashMap;

public class EvaluationCodelet extends Codelet {
    Memory questionsAndAnswersMO;

    @Override
    public void accessMemoryObjects() {
        questionsAndAnswersMO=(MemoryObject)this.getInput("QUESTIONS_AND_ANSWERS");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        if(questionsAndAnswersMO.getI()=="") {
            return;
        }
        Idea questionsAndAnswers = (Idea)questionsAndAnswersMO.getI();

        questionsAndAnswers.get("howMany").getL().stream().forEach(question -> {
            HashMap<String, Integer> trueValue = new TrueValue0().correctHowManyObjects();
            String objectType = question.getName();
            int answerCount = (int) question.getValue();
            question.setValue(answerCount-trueValue.get(objectType));
        });

        questionsAndAnswers.get("whichObjectsDestroyedByMissiles").getL().stream().forEach(question -> {
            HashMap<String, Integer> trueValue = new TrueValue0().correctWhichObjectsDestroyedByMissile();
            String objectType = question.getName();
            int answerCount = (int) question.getValue();
            question.setValue(answerCount-trueValue.get(objectType));
        });

        System.out.println(questionsAndAnswers.toStringFull());
    }
}
