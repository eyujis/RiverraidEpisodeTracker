package org.example.visualization;

import org.opencv.core.Scalar;

import java.util.HashMap;
import java.util.Random;

public class Object2Color {
    HashMap<String, Scalar> objectColors;

    public Object2Color() {
        objectColors = new HashMap<String, Scalar>();
    }

    public Scalar getColor(String objectName) {
        if(objectColors.get(objectName) == null) {
            objectColors.put(objectName, generateRandomColor());
        }

        return objectColors.get(objectName);
    }

    private Scalar generateRandomColor() {
        Random rng = new Random();
        // Random colors closer to white for avoiding dark contours with black background.
        return new Scalar(rng.nextInt(231) + 25,
                rng.nextInt(231) + 25,
                rng.nextInt(231) + 25);
    }

}
