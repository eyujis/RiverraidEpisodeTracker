package org.example.mind.codelets.object_proposer.object_label;

import br.unicamp.cst.representation.idea.Idea;

import java.util.HashMap;

public class RGB2ColorLabel {
    public HashMap<String, String> rbg2ColorLabel = new HashMap<>();
    String helixYellowRGB = "210,164,74";

    public RGB2ColorLabel() {
        rbg2ColorLabel.put(helixYellowRGB, "helixYellow");
    }

    public String getColorLabel(Idea color) {
        double r = (double) color.get("R").getValue();
        double g = (double) color.get("G").getValue();
        double b = (double) color.get("B").getValue();

        String rbgString = String.valueOf((int) r)+","
                + String.valueOf((int) g)+","
                + String.valueOf((int) b);

        return rbg2ColorLabel.get(rbgString);
    }
}
