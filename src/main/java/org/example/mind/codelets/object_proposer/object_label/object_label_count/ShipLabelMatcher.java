package org.example.mind.codelets.object_proposer.object_label.object_label_count;

public class ShipLabelMatcher extends TemplateLabelMatcher {
    public ShipLabelMatcher() {
        super();
        this.labelName = "ship";
        this.nFragments = 1;
        this.shipOrMissileYellowCount = 1;
        this.shipOrMissileContourPointCount = 64;
    }
}
