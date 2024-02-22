package org.example.mind.codelets.object_proposer;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_proposer.entities.FragmentFactory;
import org.example.mind.codelets.object_proposer.fg_samplers.FGPositionSequentialSampler;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class RandomSaccadesAlgorithm {
    private Idea unObjects;
    private FGPositionSequentialSampler fGPositionSampler;
    private FragmentFactory fragmentFactory = new FragmentFactory();

    public Idea getAllUnObjects(Mat frame) {
        Idea unObjects = new Idea("PossibleFragments", "", 0);
        fGPositionSampler = new FGPositionSequentialSampler(frame);

        while(!fGPositionSampler.isEmpty()) {
            Mat frameClone = frame.clone();
            Point randomPoint = fGPositionSampler.getFGPosition();

            double[] objColorBRG = frameClone.get((int) randomPoint.y, (int) randomPoint.x);
            Mat mask = getFloodFillMask(frameClone, randomPoint);
            List<MatOfPoint> objContour = getMaskContour(mask);

            Idea unObj = fragmentFactory.createUnFragment(objColorBRG, objContour);
            unObjects.add(unObj);

            fGPositionSampler.removeMaskFromSample(mask);
        }
        return unObjects;
    }

    private Mat getFloodFillMask(Mat frame, Point seedPoint) {
        Mat mask = Mat.zeros(frame.rows() + 2, frame.cols() + 2, 0);

        // Define floodfill parameters
        int loDiff = 0;
        int upDiff = 0;
        int flags = 4 | (255 << 8) + Imgproc.FLOODFILL_MASK_ONLY;
        Scalar fillColor = new Scalar(127, 0, 255);

        Imgproc.floodFill(frame, mask, seedPoint, fillColor, new Rect(), new Scalar(loDiff, loDiff, loDiff), new Scalar(upDiff, upDiff, upDiff), flags);

        Rect roi = new Rect(1, 1, mask.cols() - 2, mask.rows() - 2);
        mask = mask.submat(roi);

        return mask;
    }

    private List<MatOfPoint> getMaskContour(Mat mask) {
        Mat hierarchy = new Mat();
        List<MatOfPoint> contour = new ArrayList<>();

        Imgproc.findContours(mask, contour, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        return contour;
    }
}
