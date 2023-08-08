package org.example.drafts.math3;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class RealVectorExample {
    public static void main(String[] args) {
        double[] data1 = {1.0, 2.0};
        double[] data2 = {5.0, 5.0};

        RealVector vector1 = new ArrayRealVector(data1);
        RealVector vector2 = new ArrayRealVector(data2);

        RealVector sumVector = vector1.add(vector2);
        RealVector diffVector = vector1.subtract(vector2);
        RealVector elementWiseProduct = vector1.ebeMultiply(vector2);
        double dotProduct = vector1.dotProduct(vector2);

        System.out.println("Vector 1: " + vector1);
        System.out.println("Vector 2: " + vector2);
        System.out.println("Sum Vector: " + sumVector);
        System.out.println("Difference Vector: " + diffVector);
        System.out.println("Element-wise Product: " + elementWiseProduct);
        System.out.println("Dot Product: " + dotProduct);

        System.out.println(100<=Double.MAX_VALUE);
    }
}
