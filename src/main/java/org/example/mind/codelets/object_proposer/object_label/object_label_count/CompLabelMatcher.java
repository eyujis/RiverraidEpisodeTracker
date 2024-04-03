package org.example.mind.codelets.object_proposer.object_label.object_label_count;

import br.unicamp.cst.representation.idea.Idea;

import static org.example.mind.codelets.object_proposer.object_label.ObjectLabelAssigner.fragmentRgb2ColorLabel;

public class CompLabelMatcher extends TemplateLabelMatcher {
    public CompLabelMatcher() {
        super();
    }

    public void countColors(Idea fragmentCluster) {
        this.nFragments = fragmentCluster.getL().size();
        for(Idea fragment : fragmentCluster.getL()) {
            String colorLabel = fragmentRgb2ColorLabel.getColorLabel(fragment.get("color"));
            if(colorLabel!=null) {
                incrementColorCount(colorLabel);
            }
        }
    }

    private void incrementColorCount(String colorLabel) {
        if(colorLabel.equals("helicopterYellow")) {
            this.helicopterYellowCount+=1;
            return;
        }
        if(colorLabel.equals("helicopterGreen")) {
            this.helicopterGreenCount+=1;
            return;
        }
        if(colorLabel.equals("helicopterBlue")) {
            this.helicopterBlueCount+=1;
            return;
        }
        if(colorLabel.equals("fuelOrHouseWhite")) {
            this.fuelOrHouseWhiteCount +=1;
            return;
        }
        if(colorLabel.equals("fuelRed")) {
            this.fuelRedCount+=1;
            return;
        }
        if(colorLabel.equals("tankerOrHouseBlack")) {
            this.tankerOrHouseBlackCount +=1;
            return;
        }
        if(colorLabel.equals("tankerRed")) {
            this.tankerRedCount+=1;
            return;
        }
        if(colorLabel.equals("tankerBlue")) {
            this.tankerBlueCount+=1;
            return;
        }
        if(colorLabel.equals("treeGreen")) {
            this.treeGreenCount+=1;
            return;
        }
        if(colorLabel.equals("treeBrown")) {
            this.treeBrownCount+=1;
            return;
        }
        if(colorLabel.equals("shipOrMissileYellow")) {
            this.shipOrMissileYellowCount+=1;
            return;
        }
        if(colorLabel.equals("bridgeLightGrey")) {
            this.bridgeLightGreyCount+=1;
            return;
        }
        if(colorLabel.equals("bridgeBrown")) {
            this.bridgeBrownCount+=1;
            return;
        }
        if(colorLabel.equals("bridgeDarkGrey")) {
            this.bridgeDarkGreyCount+=1;
            return;
        }
        if(colorLabel.equals("bridgeDarkYellow")) {
            this.bridgeDarkYellowCount+=1;
            return;
        }
        if(colorLabel.equals("bridgeNormalYellow")) {
            this.bridgeNormalYellowCount+=1;
            return;
        }
        if(colorLabel.equals("bridgeLightYellow")) {
            this.bridgeLightYellowCount+=1;
            return;
        }
    }
}
