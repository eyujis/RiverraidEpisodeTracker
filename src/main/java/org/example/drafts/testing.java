package org.example.drafts;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class testing {
    public static void main(String[] args) {
        RealVector catVector = new ArrayRealVector(new double[]{1, 0});
        RealVector compVector = new ArrayRealVector(new double[]{0, 1});
        System.out.println((catVector.getNorm() - compVector.getNorm()) == 0);
    }
}


