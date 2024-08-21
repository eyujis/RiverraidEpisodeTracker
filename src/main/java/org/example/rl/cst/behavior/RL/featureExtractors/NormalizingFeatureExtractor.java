package org.example.rl.cst.behavior.RL.featureExtractors;

import java.util.ArrayList;

public class NormalizingFeatureExtractor extends DirectFeatureExtractor {
    @Override
    public ArrayList<Double> extractFeatures(ArrayList<Double> S) {
        return normalizeValues(super.extractFeatures(S));
    }
}
