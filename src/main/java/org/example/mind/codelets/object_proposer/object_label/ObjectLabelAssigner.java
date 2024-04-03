package org.example.mind.codelets.object_proposer.object_label;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_proposer.object_label.object_label_count.*;
import org.opencv.core.MatOfPoint;

import java.util.HashMap;
import java.util.List;

public class ObjectLabelAssigner {
    public static HashMap<String, String> objectCategoryToLabel = new HashMap<>();
    public static FragmentRGB2ColorLabel fragmentRgb2ColorLabel = new FragmentRGB2ColorLabel();
    public static LabelMatchers labelMatchers = new LabelMatchers();

    public static String getLabel(String objectCategory, Idea fragmentCluster) {
        if(objectCategoryToLabel.get(objectCategory) != null) {
            return objectCategoryToLabel.get(objectCategory);
        } else {
            CompLabelMatcher compObject = new CompLabelMatcher();
            compObject.countColors(fragmentCluster);

            // disambiguate ship and missile
            if(compObject.shipOrMissileYellowCount==1) {
                compObject.shipOrMissileContourPointCount = ((List<MatOfPoint>)fragmentCluster.getL().get(0)
                        .get("contours").getValue()).get(0).toList().size();
            }

            String labelMatched = labelMatchers.getLabel(compObject);

            if(labelMatched!=null) {
                objectCategoryToLabel.put(objectCategory, labelMatched);
                return labelMatched;
            }
        }
        return null;
    }
}
