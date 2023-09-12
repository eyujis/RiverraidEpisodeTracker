package org.example.drafts;

public class testing {
    public static void main(String[] args) {
        double[] initial = new double[3];
        double[] end = new double[3];
        for(int i=0; i<3; i++) {
            initial[i] = i;
            end[i] = i+1;
        }
        System.out.println(stateDifference(initial, end));
    }
    private static double stateDifference(double[] state1, double[] state2) {

        if(state1.length!= state2.length) {
            System.out.println("States with different dimensions");
        }

        double powerSum = 0;

        for(int i=0; i<state1.length; i++) {
            powerSum = powerSum + Math.pow((state1[i]-state2[i]), 2);
        }

        double result = Math.sqrt(powerSum);

        return result;
    }
}


