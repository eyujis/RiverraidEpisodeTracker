package org.example.visualization;

import org.opencv.core.Scalar;

import java.util.HashMap;
import java.util.Random;

public class Category2Color {
    HashMap<String, Scalar> categoryColors;

    public Category2Color() {
        categoryColors = new HashMap<String, Scalar>();
    }

    public Scalar getColor(String categoryName) {
        if(categoryColors.get(categoryName) == null) {
            categoryColors.put(categoryName, generateRandomColor());
        }

        return categoryColors.get(categoryName);
    }

    private Scalar generateRandomColor() {
        Random rng = new Random();
        // Random colors closer to white for avoiding dark contours with black background.
        return new Scalar(rng.nextInt(231) + 25,
                rng.nextInt(231) + 25,
                rng.nextInt(231) + 25);
    }

}
