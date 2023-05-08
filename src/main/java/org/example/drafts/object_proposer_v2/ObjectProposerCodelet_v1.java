package org.example.drafts.object_proposer_v2;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import org.example.util.MatBufferedImageConverter;
import org.example.visualization.JLabelImgUpdater;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ObjectProposerCodelet_v1 extends Codelet implements JLabelImgUpdater {
    Memory rawDataMO;
    Memory detectedObjectsMO;
    ObjectProposer_v2 objectProposer = new ObjectProposer_v2();
    JLabel objectsImgJLabel;
    JLabel mergedObjectsImgJLabel;

    public ObjectProposerCodelet_v1(JLabel objectsImgJLabel, JLabel mergedObjectsImgJLabel) {
        this.objectsImgJLabel = objectsImgJLabel;
        this.mergedObjectsImgJLabel = mergedObjectsImgJLabel;
    }

    @Override
    public void accessMemoryObjects() {
        rawDataMO=(MemoryObject)this.getInput("RAW_DATA_BUFFER");
        detectedObjectsMO=(MemoryObject)this.getOutput("DETECTED_OBJECTS");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        try {
            Idea rawDataBufferIdea = (Idea) rawDataMO.getI();
            List<Idea> framesIdea = (List<Idea>) rawDataBufferIdea.get("frames").getValue();
            BufferedImage frameImg = (BufferedImage) (framesIdea.get(0).getValue());

            this.objectProposer.update(MatBufferedImageConverter.BufferedImage2Mat(frameImg));

//          ----------new Object proposer----

            List<PotentialObject> potentialObjects =  objectProposer.getConfirmedObjsCF();
            BufferedImage potentialObjectsImg = buffImageFromPotentialObjectList(potentialObjects);
            updateJLabelImg(this.objectsImgJLabel, potentialObjectsImg);

            BufferedImage objectsImg = buffImageContourFromPotentialObjectList(potentialObjects);
            updateJLabelImg(this.mergedObjectsImgJLabel, objectsImg);

//          ---------------Testing--------------
//            System.out.println("---------------Object List--------------");
//            Idea detectedObjectsIdea = (Idea) detectedObjectsMO.getI();
//            List<Idea> detectedObjectsList = (List<Idea>) detectedObjectsIdea.getValue();
//            System.out.println("n_objects:" + detectedObjectsList.size());
//            for (Idea detectedObject : detectedObjectsList) {
//                System.out.println(detectedObject.toStringFull());
//            }
//          ------------------------------------

        }
        catch (Exception e) {
        }
    }

    @Override
    public void updateJLabelImg(JLabel jLabelToUpdate, BufferedImage imgToSet) {
        jLabelToUpdate.setIcon(new ImageIcon(imgToSet));
    }



    public BufferedImage buffImageFromPotentialObjectList(List<PotentialObject> potentialObjectList) throws IOException {
        Mat drawing = Mat.zeros(new Size(304, 322), CvType.CV_8UC3);

        for (int i = 0; i < potentialObjectList.size(); i++) {
            Scalar color = potentialObjectList.get(i).getColor();
            Imgproc.rectangle(drawing,
                    potentialObjectList.get(i).getBoundRect().tl(),
                    potentialObjectList.get(i).getBoundRect().br(), color, 1);
        }

        BufferedImage bufferedImage = MatBufferedImageConverter.Mat2BufferedImage(drawing);

        return bufferedImage;
    }

    public BufferedImage buffImageContourFromPotentialObjectList(List<PotentialObject> potentialObjectList) throws IOException {
        Mat drawing = Mat.zeros(new Size(304, 322), CvType.CV_8UC3);

        for (int i = 0; i < potentialObjectList.size(); i++) {
            Scalar color = potentialObjectList.get(i).getColor();
            Imgproc.rectangle(drawing,
                    potentialObjectList.get(i).getBoundRect().tl(),
                    potentialObjectList.get(i).getBoundRect().br(), color, 1);
        }
        for (int i = 0; i < potentialObjectList.size(); i++) {
            List<MatOfPoint> listOfOneContour = new ArrayList<MatOfPoint>();
            listOfOneContour.add(potentialObjectList.get(i).getContour());
            Imgproc.drawContours(drawing, listOfOneContour, 0, potentialObjectList.get(i).getColor(), -1);
        }

        BufferedImage bufferedImage = MatBufferedImageConverter.Mat2BufferedImage(drawing);

        return bufferedImage;
    }


}
//    // Draw the contours on the original image
//    Mat contourImage = image.clone();
//        for (int i = 0; i < resultContours.size(); i++) {
//        Imgproc.drawContours(contourImage, resultContours, i, new Scalar(0, 0, 255), 2);
//        }