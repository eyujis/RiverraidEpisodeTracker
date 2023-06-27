package org.example.mind.codelets.object_proposer_codelet.entities;

import br.unicamp.cst.representation.idea.Idea;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

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


    public Idea createIdObjFromUnObj(Idea unObject) {
        Idea idObject = unObject.clone();
        Idea idIdea = new Idea("id", generateFragId());
        idObject.add(idIdea);
        Idea colorIdIdea = new Idea("colorId", generateColorId());
        idObject.add(colorIdIdea);

        idObject.setName("idObject");
        return idObject;
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


    public Idea createUnObject(Idea fragmentCluster) {

        Idea objectIdea = new Idea("unObject","",0);

        Idea boundRectIdea = getClusterOutsideRect(fragmentCluster);
        objectIdea.add(boundRectIdea);

        Idea centerIdea = getCenterFromBoundRect(boundRectIdea);
        objectIdea.add(centerIdea);



//        Idea objectCategoryIdea = new Idea("objectCategory", null);
//        objectIdea.add(objectCategoryIdea);

//        Idea objectFragments = new Idea("fragments", null);
//        objectIdea.add(objectFragments);


        return objectIdea;
    }


    private Idea getClusterOutsideRect(Idea fragmentCluster) {
        Idea resultRect = fragmentCluster.getL().get(0).get("boundRect");
        for(int i=1; i<fragmentCluster.getL().size(); i++) {
            resultRect = getOutsideRect(resultRect, fragmentCluster.getL().get(i).get("boundRect"));
        }
        return resultRect;
    }

    private Idea getOutsideRect(Idea r1, Idea r2) {
        double x1Start = (double) r1.get("tl.x").getValue();
        double x1End = (double) r1.get("br.x").getValue();
        double y1Start = (double) r1.get("tl.y").getValue();
        double y1End = (double) r1.get("br.y").getValue();

        double x2Start = (double) r2.get("tl.x").getValue();
        double x2End = (double) r2.get("br.x").getValue();
        double y2Start = (double) r2.get("tl.y").getValue();
        double y2End = (double) r2.get("br.y").getValue();

        double xOutsideRectStart;
        double yOutsideRectStart;
        double xOutsideRectEnd;
        double yOutsideRectEnd;

        if(x1Start < x2Start) {
            xOutsideRectStart = x1Start;
        } else {
            xOutsideRectStart = x2Start;
        }

        if(y1Start < y2Start) {
            yOutsideRectStart = y1Start;
        } else {
            yOutsideRectStart = y2Start;
        }

        if(x1End > x2End) {
            xOutsideRectEnd = x1End;
        } else {
            xOutsideRectEnd = x2End;
        }

        if(y1End > y2End) {
            yOutsideRectEnd = y1End;
        } else {
            yOutsideRectEnd = y2End;
        }

        // bounding box
        Idea boundRectIdea = new Idea("boundRect", "", 0);
        boundRectIdea.add(new Idea("height", yOutsideRectEnd-yOutsideRectStart));
        boundRectIdea.add(new Idea("width", xOutsideRectEnd-xOutsideRectStart));

        Idea tlIdea = new Idea("tl", "", 0);
        tlIdea.add(new Idea("x", xOutsideRectStart));
        tlIdea.add(new Idea("y", yOutsideRectStart));
        boundRectIdea.add(tlIdea);

        Idea brIdea = new Idea("br", "", 0);
        brIdea.add(new Idea("x", xOutsideRectEnd));
        brIdea.add(new Idea("y", yOutsideRectEnd));
        boundRectIdea.add(brIdea);

        return boundRectIdea;

    }

    private Idea getCenterFromBoundRect(Idea boundRectIdea)   {
        double tl_x = (double) boundRectIdea.get("tl.x").getValue();
        double tl_y = (double) boundRectIdea.get("tl.y").getValue();
        double br_x = (double) boundRectIdea.get("br.x").getValue();
        double br_y = (double) boundRectIdea.get("br.y").getValue();

        double xCenter = (tl_x + br_x)/2;
        double yCenter = (tl_y + br_y)/2;

        Idea centerIdea = new Idea("center", "", 0);
        centerIdea.add(new Idea("x", xCenter));
        centerIdea.add(new Idea("y", yCenter));

        return centerIdea;
    }

    public void transferPropertyValues(Idea o1, Idea o2) {
        o1.get("boundRect.height").setValue(o2.get("boundRect.height").getValue());
        o1.get("boundRect.width").setValue(o2.get("boundRect.width").getValue());
        o1.get("boundRect.tl.x").setValue(o2.get("boundRect.tl.x").getValue());
        o1.get("boundRect.tl.y").setValue(o2.get("boundRect.tl.y").getValue());
        o1.get("boundRect.br.x").setValue(o2.get("boundRect.br.x").getValue());
        o1.get("boundRect.br.y").setValue(o2.get("boundRect.br.y").getValue());

        o1.get("center.x").setValue(o2.get("center.x").getValue());
        o1.get("center.y").setValue(o2.get("center.y").getValue());
    }

}
