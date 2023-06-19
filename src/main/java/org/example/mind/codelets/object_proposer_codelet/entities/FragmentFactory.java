package org.example.mind.codelets.object_proposer_codelet.entities;

import br.unicamp.cst.representation.idea.Idea;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.List;
import java.util.Random;

public class FragmentFactory {
    private static int factoryId = 0;

    public Idea createIdFragsFromUnFrags(Idea unFrags) {
        Idea idFrags = new Idea("idFragments", "", 0);
        for(Idea unFrag: unFrags.getL()) {
            idFrags.add(createIdFragFromUnFrag(unFrag));
        }
        return idFrags;
    }

    public Idea createIdFragFromUnFrag(Idea unFrag) {
        Idea idFrag = unFrag.clone();
        Idea idIdea = new Idea("id", generateFragId());
        idFrag.add(idIdea);
        Idea colorIdIdea = new Idea("colorId", generateColorId());
        idFrag.add(colorIdIdea);

        return idFrag;
    }

    public int generateFragId() {
        factoryId++;
        return factoryId;
    }

    private Scalar generateColorId() {
        Random rng = new Random();
        // Random colors closer to white for avoiding dark contours with black background.
        return new Scalar(rng.nextInt(231) + 25,
                rng.nextInt(231) + 25,
                rng.nextInt(231) + 25);
    }


    public Idea createUnFragment(double[] colorBGR, List<MatOfPoint> contours) {

        MatOfPoint externalContour = contours.get(0);
        Rect boundRect = getBoundRectFromContour(externalContour);
        Point centerPoint = getCenterFromBoundRect(boundRect);

        Idea fragmentIdea = new Idea("unFragment","",0);

        // color
        Idea colorIdea = new Idea("color", "", 0);
        colorIdea.add(new Idea("R", colorBGR[2]));
        colorIdea.add(new Idea("G", colorBGR[1]));
        colorIdea.add(new Idea("B", colorBGR[0]));
        fragmentIdea.add(colorIdea);

        // center
        Idea centerIdea = new Idea("center", "", 0);
        centerIdea.add(new Idea("x", centerPoint.x));
        centerIdea.add(new Idea("y", centerPoint.y));
        fragmentIdea.add(centerIdea);

        // bounding box
        Idea boundRectIdea = new Idea("boundRect", "", 0);
        boundRectIdea.add(new Idea("height", boundRect.height));
        boundRectIdea.add(new Idea("width", boundRect.width));

        Idea tlIdea = new Idea("tl", "", 0);
        tlIdea.add(new Idea("x", boundRect.tl().x));
        tlIdea.add(new Idea("y", boundRect.tl().y));
        boundRectIdea.add(tlIdea);

        Idea brIdea = new Idea("br", "", 0);
        brIdea.add(new Idea("x", boundRect.br().x));
        brIdea.add(new Idea("y", boundRect.br().y));
        boundRectIdea.add(brIdea);

        fragmentIdea.add(boundRectIdea);

        // category
        Idea pCatIdea = new Idea("FragmentCategory", null);
        fragmentIdea.add(pCatIdea);

        Idea wCatIdea = new Idea("ObjectCategory", null);
        fragmentIdea.add(wCatIdea);

        fragmentIdea.add(new Idea("contours", contours));

        // external contour
        fragmentIdea.add(new Idea("externalContour", externalContour));

        return fragmentIdea;
    }

    private Point getCenterFromBoundRect(Rect rect)   {
        Point tl = rect.tl();
        Point br = rect.br();
        double xCenter = (tl.x + br.x)/2;
        double yCenter = (tl.y + br.y)/2;

        Point center = new Point(xCenter, yCenter);
        return center;
    }

    private Rect getBoundRectFromContour(MatOfPoint externalContour) {
        MatOfPoint2f contourPoly = new MatOfPoint2f();
        Imgproc.approxPolyDP(new MatOfPoint2f(externalContour.toArray()), contourPoly, 3, true);
        Rect boundRect = Imgproc.boundingRect(new MatOfPoint(contourPoly.toArray()));
        return boundRect;
    }

    public void transferPropertyValues(Idea f1, Idea f2) {
        f1.get("color.R").setValue((double)f2.get("color.R").getValue());
        f1.get("color.B").setValue((double)f2.get("color.B").getValue());
        f1.get("color.G").setValue((double)f2.get("color.G").getValue());

        f1.get("boundRect.height").setValue(f2.get("boundRect.height").getValue());
        f1.get("boundRect.width").setValue(f2.get("boundRect.width").getValue());
        f1.get("boundRect.tl.x").setValue(f2.get("boundRect.tl.x").getValue());
        f1.get("boundRect.tl.y").setValue(f2.get("boundRect.tl.y").getValue());
        f1.get("boundRect.br.x").setValue(f2.get("boundRect.br.x").getValue());
        f1.get("boundRect.br.y").setValue(f2.get("boundRect.br.y").getValue());

        f1.get("center.x").setValue(f2.get("center.x").getValue());
        f1.get("center.y").setValue(f2.get("center.y").getValue());

        f1.get("contours").setValue(f2.get("contours").getValue());

        f1.get("externalContour").setValue(f2.get("externalContour").getValue());
    }

}
