package org.example.mind.codelets.object_proposer.entities;

import br.unicamp.cst.representation.idea.Idea;
import org.opencv.core.*;

import java.util.Random;

public class ObjectFactory {
    private static int factoryId = 0;

    public Idea createIdObjsFromUnObjs(Idea unObjs) {
        Idea idObjs = new Idea("idObjsCF", "", 0);
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


    public Idea createUnObject(Idea fragmentCluster, Idea objectCategory) {

        Idea objectIdea = new Idea("unObject","",0);

        Idea objectCategoryIdea = new Idea("objectCategory", objectCategory.getName(), 1);
        objectIdea.add(objectCategoryIdea);

        Idea boundRectIdea = getClusterOutsideRect(fragmentCluster);
        objectIdea.add(boundRectIdea);

        Idea sizeIdea = getSizeFromBoundRect(boundRectIdea);
        objectIdea.add(sizeIdea);

        Idea centerIdea = getCenterFromBoundRect(boundRectIdea);
        objectIdea.add(centerIdea);

        if((double)sizeIdea.get("height").getValue()<5
                && (double)sizeIdea.get("width").getValue()<5) {
            return null;
        }


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

        Idea boundRectIdea = new Idea("boundRect", "", 0);

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

    public Idea getSizeFromBoundRect(Idea boundRectIdea) {
        double xStart = (double) boundRectIdea.get("tl.x").getValue();
        double yStart = (double) boundRectIdea.get("tl.y").getValue();
        double xEnd = (double) boundRectIdea.get("br.x").getValue();
        double yEnd = (double) boundRectIdea.get("br.y").getValue();

        double height = yEnd-yStart;
        double width = xEnd-xStart;

        Idea sizeIdea = new Idea("size", "", 0);
        sizeIdea.add(new Idea("height", height));
        sizeIdea.add(new Idea("width", width));
        return sizeIdea;
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
        o1.get("size.height").setValue(o2.get("size.height").getValue());
        o1.get("size.width").setValue(o2.get("size.width").getValue());

        o1.get("boundRect.tl.x").setValue(o2.get("boundRect.tl.x").getValue());
        o1.get("boundRect.tl.y").setValue(o2.get("boundRect.tl.y").getValue());
        o1.get("boundRect.br.x").setValue(o2.get("boundRect.br.x").getValue());
        o1.get("boundRect.br.y").setValue(o2.get("boundRect.br.y").getValue());

        o1.get("center.x").setValue(o2.get("center.x").getValue());
        o1.get("center.y").setValue(o2.get("center.y").getValue());
    }

}
