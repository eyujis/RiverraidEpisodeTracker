package org.example.visualization;

import org.opencv.core.Scalar;

import java.util.HashMap;
import java.util.Random;

public class RelationMToRelationMI {
    static HashMap<Integer, Integer> relationsHashMap;

    public RelationMToRelationMI() {
        relationsHashMap = new HashMap<Integer, Integer>();
    }

    public void putRelationIds(Integer eventM, Integer eventMI) {
        if(relationsHashMap.get(eventMI)!=null) {
            relationsHashMap.put(eventM, relationsHashMap.get(eventMI));
        } else {
            relationsHashMap.put(eventM, eventMI);
        }
    }

    public Integer getRootMIId(Integer eventM) {
        return relationsHashMap.get(eventM);
    }

}
