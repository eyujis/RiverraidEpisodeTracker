package org.example.mind.codelets.object_proposer.object_label;

import br.unicamp.cst.representation.idea.Idea;

import java.util.HashMap;

public class FragmentRGB2ColorLabel {
    public HashMap<String, String> rgb2ColorLabel = new HashMap<>();
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
    String shipOrMissileYellowRGB = "232,232,74";
    String bridgeLightGreyRGB = "111,111,111";
    String bridgeBrownRGB = "124,44,0";
    String bridgeDarkGreyRGB = "170,170,170";
    String bridgeDarkYellowRGB = "105,105,15";
    String bridgeNormalYellowRGB = "134,134,29";
    String bridgeLightYellowRGB = "187,187,53";
    String jetBlue1RGB = "117,204,235";
    String jetBlue2RGB = "117,181,239";
    String jetPurpleRGB = "117,128,240";

    public FragmentRGB2ColorLabel() {
        rgb2ColorLabel.put(helicopterYellowRGB, "helicopterYellow");
        rgb2ColorLabel.put(helicopterGreenRGB, "helicopterGreen");
        rgb2ColorLabel.put(helicopterBlueRGB, "helicopterBlue");
        rgb2ColorLabel.put(fuelOrHouseWhiteRGB, "fuelOrHouseWhite");
        rgb2ColorLabel.put(fuelRedRGB, "fuelRed");
        rgb2ColorLabel.put(tankerOrHouseBlackRGB, "tankerOrHouseBlack");
        rgb2ColorLabel.put(tankerRedRGB, "tankerRed");
        rgb2ColorLabel.put(tankerBlueRGB, "tankerBlue");
        rgb2ColorLabel.put(treeGreenRGB, "treeGreen");
        rgb2ColorLabel.put(treeBrownRGB, "treeBrown");
        rgb2ColorLabel.put(shipOrMissileYellowRGB, "shipOrMissileYellow");
        rgb2ColorLabel.put(bridgeLightGreyRGB, "bridgeLightGrey");
        rgb2ColorLabel.put(bridgeBrownRGB, "bridgeBrown");
        rgb2ColorLabel.put(bridgeDarkGreyRGB, "bridgeDarkGrey");
        rgb2ColorLabel.put(bridgeDarkYellowRGB, "bridgeDarkYellow");
        rgb2ColorLabel.put(bridgeNormalYellowRGB, "bridgeNormalYellow");
        rgb2ColorLabel.put(bridgeLightYellowRGB, "bridgeLightYellow");
        rgb2ColorLabel.put(jetBlue1RGB, "jetBlue1");
        rgb2ColorLabel.put(jetBlue2RGB, "jetBlue2");
        rgb2ColorLabel.put(jetPurpleRGB, "jetPurple");
    }

    public String getColorLabel(Idea color) {
        double r = (double) color.get("R").getValue();
        double g = (double) color.get("G").getValue();
        double b = (double) color.get("B").getValue();

        String rbgString = String.valueOf((int) r)+","
                + String.valueOf((int) g)+","
                + String.valueOf((int) b);

        return rgb2ColorLabel.get(rbgString);
    }
}
