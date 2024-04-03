package org.example.mind.codelets.object_proposer.object_label.object_label_count;

public class TankerLabelMatcher extends TemplateLabelMatcher {
    public TankerLabelMatcher() {
        super();
        this.labelName = "tanker";
        this.nFragments = 3;
        this.tankerOrHouseBlackCount = 1;
        this.tankerRedCount = 1;
        this.tankerBlueCount = 1;
    }
}
