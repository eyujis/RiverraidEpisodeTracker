package org.example.mind.codelets.object_proposer_codelet;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_cat_learner.entities.PObjectCategory;
import org.example.mind.codelets.object_cat_learner.entities.WObjectCategory;
import org.example.mind.codelets.object_proposer_codelet.entities.IdentifiedRRObject;
import org.example.mind.codelets.object_proposer_codelet.entities.UnidentifiedRRObject;
import org.example.util.MatBufferedImageConverter;
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
        try {
            Idea rawDataBufferIdea = (Idea) rawDataMO.getI();
            List<Idea> framesIdea = (List<Idea>) rawDataBufferIdea.get("frames").getValue();
            BufferedImage buffImgFrame = (BufferedImage) (framesIdea.get(0).getValue());
            Mat frame = MatBufferedImageConverter.BufferedImage2Mat(buffImgFrame);

            this.objectProposer.update(frame);

            detectedObjectsMO.setI(this.objectProposer.getDetectedObjectsCF());

            ArrayList<PObjectCategory> pObjectCategories = (ArrayList<PObjectCategory>) objectPCategoriesMO.getI();
            objectProposer.assignPCategories(pObjectCategories);

            ArrayList<WObjectCategory> wObjectCategories = (ArrayList<WObjectCategory>) objectWCategoriesMO.getI();
            objectProposer.assignWCategories(wObjectCategories);

//          ----------visualization----------

            List<UnidentifiedRRObject> unObjs =  objectProposer.getUnObjs();
            BufferedImage unObjsBuffImg = buffImageFromUnObjectList(unObjs);
            updateJLabelImg(this.objectsImgJLabel, unObjsBuffImg);

            List<IdentifiedRRObject> idObjs = objectProposer.getIdObjsCF();
            BufferedImage idObjsBuffImg = buffImageFromIdObjectList(idObjs);
            updateJLabelImg(this.mergedObjectsImgJLabel, idObjsBuffImg);

            BufferedImage objectsImg = buffImageFromCatObjectList(idObjs);
            updateJLabelImg(this.categoriesImgJLabel, objectsImg);
        }
        catch (Exception e) {
        }
    }

    @Override
    public void updateJLabelImg(JLabel jLabelToUpdate, BufferedImage imgToSet) {
        jLabelToUpdate.setIcon(new ImageIcon(imgToSet));
    }



    public BufferedImage buffImageFromUnObjectList(List<UnidentifiedRRObject> unObjs) throws IOException {
        Mat frame = new Mat(new Size(304, 322), CvType.CV_8UC3, new Scalar(100,100,100));

        for (int i = 0; i < unObjs.size(); i++) {
            UnidentifiedRRObject unObj = unObjs.get(i);
            Imgproc.drawContours(frame,
                    unObj.getContours(),
                    -1,
                    unObj.getScalarColor(),
                    -1);
        }

        BufferedImage bufferedImage = MatBufferedImageConverter.Mat2BufferedImage(frame);

        return bufferedImage;
    }

    public BufferedImage buffImageFromIdObjectList(List<IdentifiedRRObject> idObjs) throws IOException {
        Mat frame = new Mat(new Size(304, 322), CvType.CV_8UC3, new Scalar(0,0,0));

        for (int i = 0; i < idObjs.size(); i++) {
            IdentifiedRRObject idObj = idObjs.get(i);
            Imgproc.drawContours(frame,
                    idObj.getContours(),
                    -1,
                    idObj.getColorIdScalar(),
                    -1);
        }

        BufferedImage bufferedImage = MatBufferedImageConverter.Mat2BufferedImage(frame);

        return bufferedImage;
    }

    public BufferedImage buffImageFromCatObjectList(List<IdentifiedRRObject> idObjs) throws IOException {
        Mat frame = new Mat(new Size(304, 322), CvType.CV_8UC3, new Scalar(0,0,0));

        for (int i = 0; i < idObjs.size(); i++) {
            IdentifiedRRObject idObj = idObjs.get(i);
            if(idObj.getAssignedCategory()!=null) {
                Imgproc.drawContours(frame,
                        idObj.getContours(),
                        -1,
                        idObj.getAssignedCategory().getColorIdScalar(),
                        -1);
            }
        }

        BufferedImage bufferedImage = MatBufferedImageConverter.Mat2BufferedImage(frame);

        return bufferedImage;
    }
}