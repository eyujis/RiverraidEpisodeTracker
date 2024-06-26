package org.example.mind.codelets.question_and_answering;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.RAWDataBufferizerCodelet;
import org.example.results_writer.ResultsFileWriter;

import java.util.ArrayList;


public class QuestionAndAnsweringCodelet extends Codelet {
    Memory perfectEpisodicMO;
    Memory questionsAndAnswersMO;
    Codelet rawDataBufferizerCodelet;
    boolean hasAnswered = false;

    public QuestionAndAnsweringCodelet(Codelet rawDataBufferizerCodelet) {
        this.rawDataBufferizerCodelet = rawDataBufferizerCodelet;
    }

    @Override
    public void accessMemoryObjects() {
        perfectEpisodicMO=(MemoryObject)this.getInput("PERFECT_EPISODIC_MEMORY");
        questionsAndAnswersMO=(MemoryObject)this.getOutput("QUESTIONS_AND_ANSWERS");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        if(perfectEpisodicMO.getI()=="") {
            return;
        }

        if(((RAWDataBufferizerCodelet)rawDataBufferizerCodelet).isSleep() && !hasAnswered){
            Idea perfectEpisodicMemory = (Idea) perfectEpisodicMO.getI();

            QuestionBuilder questionBuilder = new QuestionBuilder();
            Idea questions = questionBuilder.build();

            Answerer answerer = new Answerer();
            answerer.answerQuestions(questions, perfectEpisodicMemory);

            ArrayList<Integer> howManyResults = new ArrayList<>();
            for(Idea answer : questions.get("howMany").getL()) {
                howManyResults.add((int) answer.getValue());
            }
            new ResultsFileWriter().writeLineHowMany(howManyResults);

            ArrayList<Integer> whichExplodedResults = new ArrayList<>();
            for(Idea answer : questions.get("whichObjectsDestroyedByMissiles").getL()) {
                whichExplodedResults.add((int) answer.getValue());
            }
            new ResultsFileWriter().writeLineWhichDestroyed(whichExplodedResults);

            ArrayList<Integer> whenSecondFuelResults = new ArrayList<>();
            for(Idea answer : questions.get("whenSecondFuel").getL()) {
                whenSecondFuelResults.add((int) answer.getValue());
            }
            new ResultsFileWriter().writeLineWhenSecondFuel(whenSecondFuelResults);

            ArrayList<Integer> whenBridgeTargetResults = new ArrayList<>();
            for(Idea answer : questions.get("whenBridgeTarget").getL()) {
                whenBridgeTargetResults.add((int) answer.getValue());
            }
            new ResultsFileWriter().writeLineWhenBridgeTarget(whenBridgeTargetResults);

            System.out.println(questions.toStringFull());

            questionsAndAnswersMO.setI(questions);

            hasAnswered = true;

            System.exit(0);
        }
    }
}
