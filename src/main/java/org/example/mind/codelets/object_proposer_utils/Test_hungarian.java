package org.example.mind.codelets.object_proposer_utils;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

public class Test_hungarian {
    public static void main(String[] args) {
        double[][] dataMatrix = {
                {70,  40,   20,   55},
                {65,  60,   45,   90},
                {30,  45,   50,   75},
                {25,  30,   55,   40}
        };

        HungarianAlgorithm ha = new HungarianAlgorithm(dataMatrix);
        int[][] assignment = ha.findOptimalAssignment();
        for(int i=0; i<assignment.length; i++) {
            for(int j=0; j<assignment[0].length; j++) {
                System.out.println(assignment[i][j]);
            }
        }
    }
}
