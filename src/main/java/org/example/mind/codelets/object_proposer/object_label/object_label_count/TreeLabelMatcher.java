package org.example.mind.codelets.object_proposer.object_label.object_label_count;

import org.example.mind.codelets.object_proposer.object_label.ObjectLabelMatcher;

public class TreeMatcher extends ObjectLabelMatcher {
    public TreeMatcher() {
        super();
        this.labelName = "tanker";
        this.nFragments = 3;
        this.tankerOrHouseBlackCount = 1;
        this.tankerRedCount = 1;
        this.tankerBlueCount = 1;
    }
}
