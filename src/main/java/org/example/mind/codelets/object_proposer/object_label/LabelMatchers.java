package org.example.mind.codelets.object_proposer.object_label;

import org.example.mind.codelets.object_proposer.object_label.object_label_count.*;

import java.util.ArrayList;

public class LabelMatchers {
    public static ArrayList<TemplateLabelMatcher> labelMatchers;
    static {
        labelMatchers = new ArrayList<>();
        labelMatchers.add(new HelicopterLabelMatcher());
        labelMatchers.add(new FuelLabelMatcher());
        labelMatchers.add(new TankerLabelMatcher());
        labelMatchers.add(new HouseLabelMatcher());
        labelMatchers.add(new TreeLabelMatcher());
        labelMatchers.add(new ShipLabelMatcher());
        labelMatchers.add(new MissileLabelMatcher());
        labelMatchers.add(new BridgeLabelMatcher());
        labelMatchers.add(new JetLabelMatcher());
    }

    public String getLabel(CompLabelMatcher other) {
        for(TemplateLabelMatcher labelMatch: labelMatchers) {
            String matchedLabel = labelMatch.ifMatchGetLabel(other);
            if(matchedLabel!=null) {
                return matchedLabel;
            }
        }
        return null;
    }
}
