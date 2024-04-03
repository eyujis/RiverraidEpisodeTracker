package org.example.mind.codelets.object_proposer.object_label.object_label_count;

public class MissileLabelMatcher extends TemplateLabelMatcher {
    public MissileLabelMatcher() {
        super();
        this.labelName = "missile";
        this.nFragments = 1;
        this.shipOrMissileYellowCount = 1;
        this.shipOrMissileContourPointCount = 4;
    }
}
