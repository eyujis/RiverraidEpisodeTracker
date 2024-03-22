package org.example.visualization;

import org.opencv.core.Scalar;

import java.util.HashMap;
import java.util.Random;

public class RelationMToRelationMI {
    static HashMap<String, Scalar> categoryColors;

    public RelationMToRelationMI() {
        categoryColors = new HashMap<String, Scalar>();
    }

    public Scalar getColor(String sourceEventId) {
        if(categoryColors.get(sourceEventId) == null) {
            categoryColors.put(sourceEventId, generateRandomColor());
        }

        return categoryColors.get(sourceEventId);
    }

    private Scalar generateRandomColor() {
        Random rng = new Random();
        // Random colors closer to white for avoiding dark contours with black background.
        return new Scalar(rng.nextInt(231) + 25,
                rng.nextInt(231) + 25,
                rng.nextInt(231) + 25);
    }

}
