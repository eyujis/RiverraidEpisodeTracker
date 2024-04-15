package org.example.results_writer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class HowManyResultsFileWriter {
    String csvFilePath = "src/main/python/how_many.csv";

    public void createHowManyResultsFile() {
        try {
            FileWriter writer = new FileWriter(csvFilePath);

            String[] objectTypes = {"helicopter", "tanker", "fuel", "jet", "tree", "house", "bridge", "missile", "ship"};

            for(int i=0; i<objectTypes.length; i++) {
                writer.append(objectTypes[i]);
                if(i < objectTypes.length - 1) {
                    writer.append(",");
                } else {
                    writer.append("\n");
                }
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void writeLine(ArrayList<Integer> results) {
        try {
            FileWriter writer = new FileWriter(csvFilePath, true);

            for(int i=0; i<results.size(); i++) {
                writer.append(results.get(i).toString());
                if(i < results.size() - 1) {
                    writer.append(",");
                } else {
                    writer.append("\n");
                }
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing CSV file: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
