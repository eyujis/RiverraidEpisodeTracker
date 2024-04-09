package org.example.mind.codelets.evaluation.trueValues;

import java.util.HashMap;

public class TrueValue0 {

    public HashMap<String, Integer> correctHowManyObjects() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("helicopter", 10);
        map.put("tanker", 7);
        map.put("fuel", 9);
        map.put("jet", 0);
        map.put("tree", 13);
        map.put("house", 13);
        map.put("bridge", 1);
        map.put("missile", 9);
        map.put("ship", 1);
        return map;
    }

    public HashMap<String, Integer> correctWhichObjectsDestroyedByMissile() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("helicopter", 2);
        map.put("tanker", 2); // does the ship tip, counts? if yes, change to 1
        map.put("fuel", 0);
        map.put("jet", 0);
        map.put("tree", 0);
        map.put("house", 0);
        map.put("bridge", 1);
        map.put("missile", 0);
        map.put("ship", 0);
        return map;
    }
}
