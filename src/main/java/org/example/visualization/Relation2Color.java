package org.example.visualization;

import org.opencv.core.Scalar;

import java.util.HashMap;
import java.util.Random;

public class Relation2Color {
    static HashMap<Integer, Scalar> categoryColors;

    public Relation2Color() {
        categoryColors = new HashMap<Integer, Scalar>();
    }

    public Scalar getColor(Integer sourceEventId) {
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
