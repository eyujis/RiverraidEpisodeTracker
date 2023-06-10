package org.example.mind.codelets.object_proposer_codelet;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_cat_learner.entities.ObjectCategory;
import org.example.mind.codelets.object_cat_learner.entities.PObjectCategory;
import org.example.mind.codelets.object_cat_learner.entities.WObjectCategory;
import org.example.util.MatBufferedImageConverter;
import org.example.visualization.Category2Color;
import org.example.visualization.JLabelImgUpdater;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ObjectProposerCodelet extends Codelet implements JLabelImgUpdater {
    Memory rawDataMO;
    Memory detectedObjectsMO;
    Memory objectPCategoriesMO;
    Memory objectWCategoriesMO;

    ObjectProposer objectProposer = new ObjectProposer();
    JLabel objectsImgJLabel;
    JLabel mergedObjectsImgJLabel;
    JLabel categoriesImgJLabel;
    Category2Color category2Color = new Category2Color();

    public ObjectProposerCodelet(JLabel objectsImgJLabel,
                                 JLabel mergedObjectsImgJLabel,
                                 JLabel categoriesImgJLabel) {
        this.objectsImgJLabel = objectsImgJLabel;
        this.mergedObjectsImgJLabel = mergedObjectsImgJLabel;
        this.categoriesImgJLabel = categoriesImgJLabel;
    }

    @Override
    public void accessMemoryObjects() {
        rawDataMO=(MemoryObject)this.getInput("RAW_DATA_BUFFER");
        objectPCategoriesMO=(MemoryObject)this.getInput("OBJECT_PCATEGORIES");
        objectWCategoriesMO=(MemoryObject)this.getInput("OBJECT_WCATEGORIES");
        detectedObjectsMO=(MemoryObject)this.getOutput("DETECTED_OBJECTS");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {

        Idea rawDataBufferIdea = (Idea) rawDataMO.getI();
        List<Idea> framesIdea = (List<Idea>) rawDataBufferIdea.get("frames").getValue();
        BufferedImage buffImgFrame = (BufferedImage) (framesIdea.get(0).getValue());
        Mat frame = null;

        frame = MatBufferedImageConverter.BufferedImage2Mat(buffImgFrame);

        this.objectProposer.update(frame);

//      clone the detected objects later maybe here or within the bufferizer
        detectedObjectsMO.setI(this.objectProposer.getDetectedObjectsCF());

        if(objectPCategoriesMO.getI() != "") {
            Idea pObjectCategories = (Idea) objectPCategoriesMO.getI();
            objectProposer.assignPCategories(pObjectCategories);
        }


            Idea wObjectCategories = (Idea) objectWCategoriesMO.getI();
            objectProposer.assignWCategories(wObjectCategories);

//          ----------visualization----------
        try {
            Idea unObjs =  objectProposer.getUnObjs();
            BufferedImage unObjsBuffImg = buffImageFromUnObjectList(unObjs);
            updateJLabelImg(this.objectsImgJLabel, unObjsBuffImg);

            Idea idObjs = objectProposer.getIdObjsCF();
            BufferedImage idObjsBuffImg = buffImageFromIdObjectList(idObjs);
            updateJLabelImg(this.mergedObjectsImgJLabel, idObjsBuffImg);

            BufferedImage objectsImg = buffImageFromCatObjectList(idObjs);
            updateJLabelImg(this.categoriesImgJLabel, objectsImg);

        } catch (Exception err) {
            System.out.println(err.getMessage());
        }
    }

    @Override
    public void updateJLabelImg(JLabel jLabelToUpdate, BufferedImage imgToSet) {
        jLabelToUpdate.setIcon(new ImageIcon(imgToSet));
    }



    public BufferedImage buffImageFromUnObjectList(Idea unObjs) throws IOException {
        Mat frame = new Mat(new Size(304, 322), CvType.CV_8UC3, new Scalar(100,100,100));

        for (int i = 0; i < unObjs.getL().size(); i++) {
            Idea unObj = unObjs.getL().get(i);
            Imgproc.drawContours(frame,
                    (List<MatOfPoint>) unObj.get("contours").getValue(),
                    -1,
                    new Scalar((double) unObj.get("color.B").getValue(),
                            (double) unObj.get("color.G").getValue(),
                            (double) unObj.get("color.R").getValue()
                    ),
                    -1);
        }

        BufferedImage bufferedImage = MatBufferedImageConverter.Mat2BufferedImage(frame);

        return bufferedImage;
    }

    public BufferedImage buffImageFromIdObjectList(Idea idObjs) throws IOException {
        Mat frame = new Mat(new Size(304, 322), CvType.CV_8UC3, new Scalar(0,0,0));

        for (int i = 0; i < idObjs.getL().size(); i++) {
            Idea idObj = idObjs.getL().get(i);
            Imgproc.drawContours(frame,
                    (List<MatOfPoint>) idObj.get("contours").getValue(),
                    -1,
                    (Scalar) idObj.get("colorId").getValue(),
                    -1);
        }

        BufferedImage bufferedImage = MatBufferedImageConverter.Mat2BufferedImage(frame);

        return bufferedImage;
    }

    public BufferedImage buffImageFromCatObjectList(Idea idObjs) throws IOException {
        Mat frame = new Mat(new Size(304, 322), CvType.CV_8UC3, new Scalar(0,0,0));

        for (int i = 0; i < idObjs.getL().size(); i++) {
            Idea idObj = idObjs.getL().get(i);
            if(idObj.get("wCategory").getValue()!="null") {
                String objCatName = (String) idObj.get("wCategory").getValue();
                if(objCatName!=null) {
                    Imgproc.drawContours(frame,
                            (List<MatOfPoint>) idObj.get("contours").getValue(),
                            -1,
                            category2Color.getColor(objCatName),
                            -1);
                }
            } else if(idObj.get("pCategory").getValue()!="null") {
                String objCatName = (String) idObj.get("pCategory").getValue();
                if(objCatName!=null) {
                    Imgproc.drawContours(frame,
                            (List<MatOfPoint>) idObj.get("contours").getValue(),
                            -1,
                            category2Color.getColor(objCatName),
                            -1);
                }
            }
        }

        BufferedImage bufferedImage = MatBufferedImageConverter.Mat2BufferedImage(frame);

        return bufferedImage;
    }
}