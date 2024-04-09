package org.example.mind.codelets.evaluation.trueValues;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.question_and_answering.QuestionBuilder;

import java.util.HashMap;

public class TrueValue3 {
    QuestionBuilder qb = new QuestionBuilder();

    public TrueValue3() {
        Idea question = qb.build();
    }

    public HashMap<String, Integer> correctObjectTypeToNumberOfObjects() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("helicopter", 0);
        map.put("tanker", 0);
        map.put("fuel", 0);
        map.put("jet", 0);
        map.put("tree", 0);
        map.put("house", 0);
        map.put("bridge", 0);
        map.put("missile", 0);
        map.put("ship", 0);
        return map;
    }
}
