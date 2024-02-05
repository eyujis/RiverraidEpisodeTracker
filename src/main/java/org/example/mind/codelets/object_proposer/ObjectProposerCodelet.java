package org.example.mind.codelets.object_proposer;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import org.example.util.MatBufferedImageConverter;
import org.example.visualization.Category2Color;
import org.example.visualization.JLabelImgUpdater;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class ObjectProposerCodelet extends Codelet {
    Memory rawDataMO;
    Memory detectedFragmentsMO;
    Memory detectedObjectsMO;
    Memory fragmentCategoriesMO;
    Memory objectCategoriesMO;

    FragmentProposer fragmentProposer = new FragmentProposer();
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
        fragmentCategoriesMO =(MemoryObject)this.getInput("FRAGMENT_CATEGORIES");
        objectCategoriesMO=(MemoryObject)this.getInput("OBJECT_CATEGORIES");
        detectedFragmentsMO =(MemoryObject)this.getOutput("DETECTED_FRAGMENTS");
        detectedObjectsMO=(MemoryObject)this.getOutput("DETECTED_OBJECTS");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {

        Idea rawDataBufferIdea = (Idea) rawDataMO.getI();
        int buffSize = rawDataBufferIdea.getL().size();
        BufferedImage buffImgFrame = (BufferedImage) rawDataBufferIdea.getL().get(buffSize-1).get("image").getValue();

        Mat image = null;
        image = MatBufferedImageConverter.BufferedImage2Mat(buffImgFrame);
        this.fragmentProposer.update(image);

        if(fragmentCategoriesMO.getI() != "") {
            Idea fragmentCategories = (Idea) fragmentCategoriesMO.getI();
            fragmentProposer.assignFragmentCategories(fragmentCategories);
        }

        if(objectCategoriesMO.getI() != "") {
            Idea objectCategories = (Idea) objectCategoriesMO.getI();
            fragmentProposer.assignObjectCategories(objectCategories);

            objectProposer.update(fragmentProposer.getDetectedFragmentsCF(), objectCategories);
        }


        detectedFragmentsMO.setI(this.fragmentProposer.getDetectedFragmentsCF());

        Idea detectedObjects = new Idea("detectedObjects", "", 0);
        detectedObjects.add(this.objectProposer.getDetectedObjectsCF());
        detectedObjects.add(rawDataBufferIdea.getL().get(buffSize-1).get("timestamp"));
        detectedObjects.get("idObjsCF").setName("objects");
        detectedObjectsMO.setI(detectedObjects);

//        System.out.println(((Idea) detectedObjectsMO.getI()).toStringFull());

//          ----------visualization----------
        try {
            Idea unFrags =  fragmentProposer.getUnFrags();
            BufferedImage unFragsBuffImg = buffImageFromUnObjectList(unFrags);
            updateJLabelImg(this.objectsImgJLabel, unFragsBuffImg);

            Idea idFrags = fragmentProposer.getDetectedFragmentsCF();
            BufferedImage idFragsBuffImg = buffImageFromIdObjectList(idFrags);
            updateJLabelImg(this.mergedObjectsImgJLabel, idFragsBuffImg);

            Idea idObjs = objectProposer.getDetectedObjectsCF();
            BufferedImage objectsImg = buffImageFromCatObjectList(idFrags, idObjs);
            updateJLabelImg(this.categoriesImgJLabel, objectsImg);

        } catch (Exception err) {
            System.out.println(err.getMessage());
        }
    }

    public void updateJLabelImg(JLabel jLabelToUpdate, BufferedImage imgToSet) {
        jLabelToUpdate.setIcon(new ImageIcon(imgToSet));
        jLabelToUpdate.revalidate();
        jLabelToUpdate.repaint();
        jLabelToUpdate.update(jLabelToUpdate.getGraphics());
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

    public BufferedImage buffImageFromCatObjectList(Idea idFrags, Idea idObjs) throws IOException {
        Mat frame = new Mat(new Size(304, 322), CvType.CV_8UC3, new Scalar(0,0,0));

        for (int i = 0; i < idFrags.getL().size(); i++) {
            Idea idObj = idFrags.getL().get(i);
            if(idObj.get("ObjectCategory").getValue()!="null") {
                String objCatName = (String) idObj.get("ObjectCategory").getValue();
                if(objCatName!=null) {
                    Imgproc.drawContours(frame,
                            (List<MatOfPoint>) idObj.get("contours").getValue(),
                            -1,
                            category2Color.getColor(objCatName),
                            -1);
                }
            } else if(idObj.get("FragmentCategory").getValue()!="null") {
                String objCatName = (String) idObj.get("FragmentCategory").getValue();
                if(objCatName!=null) {
                    Imgproc.drawContours(frame,
                            (List<MatOfPoint>) idObj.get("contours").getValue(),
                            -1,
                            category2Color.getColor(objCatName),
                            -1);
                }
            }
        }
        if (idObjs!=null && idObjs.getL().size()>0) {
            for(int i = 0; i < idObjs.getL().size(); i++) {
                Idea boundRectIdea = idObjs.getL().get(i).get("boundRect");

                double tl_x = (double) boundRectIdea.get("tl.x").getValue();
                double tl_y = (double) boundRectIdea.get("tl.y").getValue();
                double br_x = (double) boundRectIdea.get("br.x").getValue();
                double br_y = (double) boundRectIdea.get("br.y").getValue();

                Scalar colorId = (Scalar) idObjs.getL().get(i).get("colorId").getValue();

                Imgproc.rectangle(frame, new Point(tl_x, tl_y), new Point(br_x, br_y), colorId, 2);

                String text = idObjs.getL().get(i).get("id").getValue().toString();
                Point textOrg = new Point(tl_x, tl_y);
                int fontFace = Imgproc.FONT_HERSHEY_SIMPLEX;
                double fontScale = 0.5;
                Scalar textColor = colorId;
                int textThickness = 2;

                Imgproc.putText(frame, text, textOrg, fontFace, fontScale, textColor, textThickness);
            }
        }


        BufferedImage bufferedImage = MatBufferedImageConverter.Mat2BufferedImage(frame);

        return bufferedImage;
    }
}