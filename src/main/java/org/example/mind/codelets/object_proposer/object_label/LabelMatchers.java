package org.example.mind.codelets.object_proposer.object_label.object_label_count;

import org.example.mind.codelets.object_proposer.object_label.ObjectLabelMatcher;

import java.util.ArrayList;

public class LabelMatchers {
    public static ArrayList<ObjectLabelMatcher> labelMatches;
    static {
        labelMatches = new ArrayList<>();
        labelMatches.add(new HelicopterLabelMatcher());
        labelMatches.add(new FuelLabelMatcher());
        labelMatches.add(new TankerMatcher());
        labelMatches.add(new HouseLabelMatcher());
    }

    public String getLabel(CompMatcherObject other) {
        for(ObjectLabelMatcher labelMatch: labelMatches) {
            String matchedLabel = labelMatch.ifMatchGetLabel(other);
            if(matchedLabel!=null) {
                return matchedLabel;
            }
        }
        return null;
    }
}
