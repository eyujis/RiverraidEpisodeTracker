package org.example.drafts;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class testing {
    public static void main(String[] args) {
        // File path for the CSV file
        String filePath = "data.csv";

        // Sample data to write to the CSV file
        String[][] data = {
                {"Name", "Age", "Country"},
                {"John", "25", "USA"},
                {"Alice", "30", "Canada"},
                {"Bob", "28", "UK"},
                {"Bob", "28", "UK", "whoa"}
        };

        // Write data to the CSV file
        try {
            FileWriter fw = new FileWriter(filePath);
            PrintWriter pw = new PrintWriter(fw);

            for (String[] rowData : data) {
                pw.println(String.join(",", rowData));
            }

            pw.flush();
            pw.close();
            System.out.println("CSV file written successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


