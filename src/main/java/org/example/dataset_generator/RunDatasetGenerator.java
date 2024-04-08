package org.example.dataset_generator;

import java.io.IOException;

public class RunDatasetGenerator {
    public static void main(String[] args) throws IOException, InterruptedException {
        DatasetGenerator datasetGenerator = new DatasetGenerator();
        datasetGenerator.run();
    }
}
