package org.example.mind.codelets;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_proposer_utils.ObjectProposer;
import org.example.mind.codelets.object_proposer_utils.entities.IdentifiedObject;
import org.example.mind.codelets.object_proposer_utils.entities.UnidentifiedObject;
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
    ObjectProposer objectProposer = new ObjectProposer();
    JLabel objectsImgJLabel;
    JLabel mergedObjectsImgJLabel;

    public ObjectProposerCodelet(JLabel objectsImgJLabel, JLabel mergedObjectsImgJLabel) {
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
            BufferedImage buffImgFrame = (BufferedImage) (framesIdea.get(0).getValue());
            Mat frame = MatBufferedImageConverter.BufferedImage2Mat(buffImgFrame);

            this.objectProposer.update(frame);

//          ----------new Object proposer----

            List<UnidentifiedObject> unObjs =  objectProposer.getUnObjs();
            BufferedImage unObjsBuffImg = buffImageFromUnObjectList(unObjs);
            updateJLabelImg(this.objectsImgJLabel, unObjsBuffImg);

            List<IdentifiedObject> idObjs = objectProposer.getIdObjsCF();
            BufferedImage idObjsBuffImg = buffImageFromIdObjectList(idObjs);
            updateJLabelImg(this.mergedObjectsImgJLabel, idObjsBuffImg);

//            BufferedImage objectsImg = buffImageContourFromPotentialObjectList(potentialObjects);
//            updateJLabelImg(this.mergedObjectsImgJLabel, objectsImg);

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



    public BufferedImage buffImageFromUnObjectList(List<UnidentifiedObject> unObjs) throws IOException {
        Mat frame = new Mat(new Size(304, 322), CvType.CV_8UC3, new Scalar(100,100,100));

        for (int i = 0; i < unObjs.size(); i++) {
            UnidentifiedObject unObj = unObjs.get(i);
            Imgproc.drawContours(frame,
                    unObj.getContours(),
                    -1,
                    unObj.getScalarColor(),
                    -1);
        }

        BufferedImage bufferedImage = MatBufferedImageConverter.Mat2BufferedImage(frame);

        return bufferedImage;
    }

    public BufferedImage buffImageFromIdObjectList(List<IdentifiedObject> idObjs) throws IOException {
        Mat frame = new Mat(new Size(304, 322), CvType.CV_8UC3, new Scalar(0,0,0));

        for (int i = 0; i < idObjs.size(); i++) {
            IdentifiedObject idObj = idObjs.get(i);
            Imgproc.drawContours(frame,
                    idObj.getContours(),
                    -1,
                    idObj.getColorIdScalar(),
                    -1);
        }

        BufferedImage bufferedImage = MatBufferedImageConverter.Mat2BufferedImage(frame);

        return bufferedImage;
    }
}