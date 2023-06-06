package org.example.mind.codelets.object_proposer_codelet.entities;

import br.unicamp.cst.representation.idea.Category;
import br.unicamp.cst.representation.idea.Idea;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import javax.sound.midi.SysexMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ObjectFactory {
    private static int factoryId = 0;

    public Idea createIdObjsFromUnObjs(Idea unObjs) {
        Idea idObjs = new Idea("idObjects", "", 0);
        for(Idea unObj: unObjs.getL()) {
            idObjs.add(createIdObjFromUnObj(unObj));
        }
        return idObjs;
    }

    public Idea createIdObjFromUnObj(Idea unObj) {
        Idea idObject = unObj.clone();
        Idea idIdea = new Idea("id", generateObjId());
        idObject.add(idIdea);
        Idea colorIdIdea = new Idea("colorId", generateColorId());
        idObject.add(colorIdIdea);

        return idObject;
    }

    public int generateObjId() {
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


    public Idea createUnObject(double[] colorBGR, List<MatOfPoint> contours) {

        MatOfPoint externalContour = contours.get(0);
        Rect boundRect = getBoundRectFromContour(externalContour);
        Point centerPoint = getCenterFromBoundRect(boundRect);

        Idea objectIdea = new Idea("object","",0);

        // color
        Idea colorIdea = new Idea("color", "", 0);
        colorIdea.add(new Idea("R", colorBGR[2]));
        colorIdea.add(new Idea("G", colorBGR[1]));
        colorIdea.add(new Idea("B", colorBGR[0]));
        objectIdea.add(colorIdea);

        // center
        Idea centerIdea = new Idea("center", "", 0);
        centerIdea.add(new Idea("x", centerPoint.x));
        centerIdea.add(new Idea("y", centerPoint.y));
        objectIdea.add(centerIdea);

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

        objectIdea.add(boundRectIdea);

//        // category
        Idea catIdea = new Idea("category", null);
        objectIdea.add(catIdea);

        objectIdea.add(new Idea("contours", contours));

        // external contour
        objectIdea.add(new Idea("externalContour", externalContour));

        return objectIdea;
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

    public void transferPropertyValues(Idea obj1, Idea obj2) {
        obj1.get("color.R").setValue((double)obj2.get("color.R").getValue());
        obj1.get("color.B").setValue((double)obj2.get("color.B").getValue());
        obj1.get("color.G").setValue((double)obj2.get("color.G").getValue());

        obj1.get("boundRect.height").setValue(obj2.get("boundRect.height").getValue());
        obj1.get("boundRect.width").setValue(obj2.get("boundRect.width").getValue());
        obj1.get("boundRect.tl.x").setValue(obj2.get("boundRect.tl.x").getValue());
        obj1.get("boundRect.tl.y").setValue(obj2.get("boundRect.tl.y").getValue());
        obj1.get("boundRect.br.x").setValue(obj2.get("boundRect.br.x").getValue());
        obj1.get("boundRect.br.y").setValue(obj2.get("boundRect.br.y").getValue());

        obj1.get("center.x").setValue(obj2.get("center.x").getValue());
        obj1.get("center.y").setValue(obj2.get("center.y").getValue());

        obj1.get("contours").setValue(obj2.get("contours").getValue());

        obj1.get("externalContour").setValue(obj2.get("externalContour").getValue());
    }

}
