package org.example.results_writer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ResultsFileWriter {
    String csvHowManyFilePath = "src/main/python/how_many.csv";
    String csvWhichExplodedFilePath = "src/main/python/which_destroyed.csv";
    String csvWhenSecondFuel = "src/main/python/when_second_fuel.csv";
    String csvWhenBridgeTarget = "src/main/python/when_bridge_target.csv";

    public void createHowManyResultsFile() {
        try {
            FileWriter writer = new FileWriter(csvHowManyFilePath);

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

    public void createWhichDestroyedResultsFile() {
        try {
            FileWriter writer = new FileWriter(csvWhichExplodedFilePath);

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

    public void createWhenSecondFuelResultsFile() {
        try {
            FileWriter writer = new FileWriter(csvWhenSecondFuel);

            String[] fields = {"move_start", "move_end", "appeared", "disappeared"};

            for(int i=0; i<fields.length; i++) {
                writer.append(fields[i]);
                if(i < fields.length - 1) {
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

    public void createWhenBridgeTargetResultsFile() {
        try {
            FileWriter writer = new FileWriter(csvWhenBridgeTarget);

            String[] fields = {"bridge_appear", "missile_launch", "explosion"};

            for(int i=0; i<fields.length; i++) {
                writer.append(fields[i]);
                if(i < fields.length - 1) {
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

    public void writeLineHowMany(ArrayList<Integer> results) {
        try {
            FileWriter writer = new FileWriter(csvHowManyFilePath, true);

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

    public void writeLineWhichDestroyed(ArrayList<Integer> results) {
        try {
            FileWriter writer = new FileWriter(csvWhichExplodedFilePath, true);

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

    public void writeLineWhenSecondFuel(ArrayList<Integer> results) {
        try {
            FileWriter writer = new FileWriter(csvWhenSecondFuel, true);

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

    public void writeLineWhenBridgeTarget(ArrayList<Integer> results) {
        try {
            FileWriter writer = new FileWriter(csvWhenBridgeTarget, true);

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
