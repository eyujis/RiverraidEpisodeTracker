package org.example.mind.codelets.object_proposer.object_label.object_label_count;

public class shipLabelMatcher extends TemplateLabelMatcher {
    public shipLabelMatcher() {
        super();
        this.labelName = "fuel";
        this.nFragments = 4;
        this.fuelOrHouseWhiteCount = 2;
        this.fuelRedCount = 2;
    }
}
