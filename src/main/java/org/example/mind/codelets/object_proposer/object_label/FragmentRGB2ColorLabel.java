package org.example.mind.codelets.object_proposer.object_label;

import br.unicamp.cst.representation.idea.Idea;

import java.util.HashMap;

public class FragmentRGB2ColorLabel {
    public HashMap<String, String> rbg2ColorLabel = new HashMap<>();
    String helicopterYellowRGB = "210,164,74";
    String helicopterGreenRGB = "0,64,48";
    String helicopterBlueRGB = "0,0,148";
    String fuelOrHouseWhiteRGB = "214,214,214";
    String fuelRedRGB = "214,92,92";
    String tankerOrHouseBlackRGB = "0,0,0";
    String tankerRedRGB = "163,57,21";
    String tankerBlueRGB = "84,160,197";
    String treeGreenRGB = "158,208,101";
    String treeBrownRGB = "72,72,0";
    String shipOrMissileYellowRGB = "232,232,74"; //2
    String bridgeLightGreyRGB = "111,111,111"; //4
    String bridgeBrownRGB = "124,44,0"; //2
    String bridgeDarkGreyRGB = "170,170,170"; //4
    String bridgeDarkYellowRGB = "105,105,15"; //4
    String bridgeNormalYellowRGB = "134,134,29"; //4
    String bridgeLightYellowRGB = "187,187,53"; //1



    public FragmentRGB2ColorLabel() {
        rbg2ColorLabel.put(helicopterYellowRGB, "helicopterYellow");
        rbg2ColorLabel.put(helicopterGreenRGB, "helicopterGreen");
        rbg2ColorLabel.put(helicopterBlueRGB, "helicopterBlue");
        rbg2ColorLabel.put(fuelOrHouseWhiteRGB, "fuelOrHouseWhite");
        rbg2ColorLabel.put(fuelRedRGB, "fuelRed");
        rbg2ColorLabel.put(tankerOrHouseBlackRGB, "tankerOrHouseBlack");
        rbg2ColorLabel.put(tankerRedRGB, "tankerRed");
        rbg2ColorLabel.put(tankerBlueRGB, "tankerBlue");
        rbg2ColorLabel.put(treeGreenRGB, "treeGreen");
        rbg2ColorLabel.put(treeBrownRGB, "treeBrown");
        rbg2ColorLabel.put(shipOrMissileYellowRGB, "shipOrMissileYellow");
        rbg2ColorLabel.put(bridgeLightGreyRGB, "bridgeLightGrey");
        rbg2ColorLabel.put(bridgeBrownRGB, "bridgeBrown");
        rbg2ColorLabel.put(bridgeDarkGreyRGB, "bridgeDarkGrey");
        rbg2ColorLabel.put(bridgeDarkYellowRGB, "bridgeDarkYellow");
        rbg2ColorLabel.put(bridgeNormalYellowRGB, "bridgeNormalYellow");
        rbg2ColorLabel.put(bridgeLightYellowRGB, "bridgeLightYellow");


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
