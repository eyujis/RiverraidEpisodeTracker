package org.example.mind.codelets.question_and_answering;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.RAWDataBufferizerCodelet;
import org.example.results_writer.HowManyResultsFileWriter;

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

            ArrayList<Integer> results = new ArrayList<>();
            for(Idea answer : questions.get("howMany").getL()) {
                results.add((int) answer.getValue());
            }
            new HowManyResultsFileWriter().writeLine(results);

            System.out.println(questions.toStringFull());

            questionsAndAnswersMO.setI(questions);

            hasAnswered = true;

            System.exit(0);
        }
    }
}