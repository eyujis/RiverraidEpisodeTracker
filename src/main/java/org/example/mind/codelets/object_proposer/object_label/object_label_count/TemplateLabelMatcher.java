package org.example.mind.codelets.object_proposer.object_label.object_label_count;

public class TemplateLabelMatcher {
    public String labelName = null;
    public int nFragments = -1;
    public int helicopterYellowCount = 0;
    public int helicopterGreenCount = 0;
    public int helicopterBlueCount = 0;
    public int fuelOrHouseWhiteCount = 0;
    public int fuelRedCount = 0;
    public int tankerOrHouseBlackCount = 0;
    public int tankerRedCount = 0;
    public int tankerBlueCount = 0;
    public int treeGreenCount = 0;
    public int treeBrownCount = 0;
    public int shipOrMissileYellowCount = 0;
    public int shipOrMissileContourPointCount = -1;
    public int bridgeBrownCount = 0;
    public int bridgeDarkYellowCount = 0;
    public int bridgeNormalYellowCount = 0;
    public int bridgeLightYellowCount = 0;
    public int jetBlue1Count = 0;
    public int jetBlue2Count = 0;
    public int jetPurpleCount = 0;


    public String ifMatchGetLabel(CompLabelMatcher other) {
        if(isMatch(other)) {
            return this.labelName;
        } else {
            return null;
        }
    }

    public boolean isMatch(TemplateLabelMatcher other) {
        return this.nFragments == other.nFragments && this.nFragments != -1
                && this.helicopterYellowCount == other.helicopterYellowCount
                && this.helicopterGreenCount == other.helicopterGreenCount
                && this.helicopterBlueCount == other.helicopterBlueCount
                && this.fuelOrHouseWhiteCount == other.fuelOrHouseWhiteCount
                && this.fuelRedCount == other.fuelRedCount
                && this.tankerOrHouseBlackCount == other.tankerOrHouseBlackCount
                && this.tankerRedCount == other.tankerRedCount
                && this.tankerBlueCount == other.tankerBlueCount
                && this.treeGreenCount == other.treeGreenCount
                && this.treeBrownCount == other.treeBrownCount
                && this.shipOrMissileYellowCount == other.shipOrMissileYellowCount
                && this.shipOrMissileContourPointCount == other.shipOrMissileContourPointCount
                && this.bridgeBrownCount == other.bridgeBrownCount
                && this.bridgeDarkYellowCount == other.bridgeDarkYellowCount
                && this.bridgeNormalYellowCount == other.bridgeNormalYellowCount
                && this.bridgeLightYellowCount == other.bridgeLightYellowCount
                && this.jetBlue1Count == other.jetBlue1Count
                && this.jetBlue2Count == other.jetBlue2Count
                && this.jetPurpleCount == other.jetPurpleCount;
    }
}
