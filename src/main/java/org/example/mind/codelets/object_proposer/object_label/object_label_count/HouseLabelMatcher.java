package org.example.mind.codelets.object_proposer.object_label.object_label_count;

public class HouseLabelMatcher extends TemplateLabelMatcher {
    public HouseLabelMatcher() {
        super();
        this.labelName = "house";
        this.nFragments = 2;
        this.fuelOrHouseWhiteCount = 1;
        this.tankerOrHouseBlackCount = 1;
    }
}
