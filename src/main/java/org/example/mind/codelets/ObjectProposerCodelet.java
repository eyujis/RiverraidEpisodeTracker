package org.example.mind.codelets;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_detection.ComposedObject;
import org.example.mind.codelets.object_detection.IndividualObject;
import org.example.mind.codelets.object_detection.ObjectListProposer;
import org.example.util.MatBufferedImageConverter;
import org.example.visualization.JLabelImgUpdater;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ObjectProposerCodelet extends Codelet implements JLabelImgUpdater {
    Memory rawDataMO;
    Memory detectedObjectsMO;
    ObjectListProposer objectListProposer = new ObjectListProposer();
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
            BufferedImage frameImg = (BufferedImage) (framesIdea.get(0).getValue());

            this.objectListProposer.update(MatBufferedImageConverter.BufferedImage2Mat(frameImg));

//          -----------Visualization-----------
//            List<IndividualObject> individualObjectList = objectListProposer.getIndividualObjectListFromFrame(MatBufferedImageConverter.BufferedImage2Mat(frameImg));
//            BufferedImage individualObjectsImg = buffImageFromIndividualObjectList(individualObjectList);
//            updateJLabelImg(this.objectsImgJLabel, individualObjectsImg);

            List<ComposedObject> composedObjectList = objectListProposer.getComposedObjectListFromCurrentFrame();
            BufferedImage objectsImg = buffImageFromObjectList(composedObjectList);
            updateJLabelImg(this.objectsImgJLabel, objectsImg);

            List<ComposedObject> mergedObjectList = objectListProposer.getComposedObjectMergedListFromCurrentFrame();
            BufferedImage mergedObjectImg = buffImageFromObjectList(mergedObjectList);
            updateJLabelImg(this.mergedObjectsImgJLabel, mergedObjectImg);
//          ------------------------------------

            detectedObjectsMO.setI(getDetectedObjectsIdea(mergedObjectList));

//          ---------------Testing--------------
            Idea detectedObjectsIdea = (Idea) detectedObjectsMO.getI();
            List<Idea> detectedObjectsList = (List<Idea>) detectedObjectsIdea.getValue();
            for (Idea detectedObject : detectedObjectsList) {
                System.out.println(detectedObject.toStringFull());
            }
//          ------------------------------------

        }
        catch (Exception e) {
        }
    }

    @Override
    public void updateJLabelImg(JLabel jLabelToUpdate, BufferedImage imgToSet) {
        jLabelToUpdate.setIcon(new ImageIcon(imgToSet));
    }

    public BufferedImage buffImageFromObjectList(List<ComposedObject> composedObjectList) throws IOException {
        Mat drawing = Mat.zeros(new Size(304, 322), CvType.CV_8UC3);

        for (int i = 0; i < composedObjectList.size(); i++) {
            Scalar color = composedObjectList.get(i).getCurrentFrameIndividualObject().getColor();
            Imgproc.rectangle(drawing,
                    composedObjectList.get(i).getCurrentFrameIndividualObject().getBoundRect().tl(),
                    composedObjectList.get(i).getCurrentFrameIndividualObject().getBoundRect().br(), color, 1);
        }

        BufferedImage bufferedImage = MatBufferedImageConverter.Mat2BufferedImage(drawing);

        return bufferedImage;
    }

    public Idea getDetectedObjectsIdea(List<ComposedObject> mergedObjectList) {
        Idea detectedObjectsIdea = new Idea("detectedObjects", null);
        List<Idea> detectedObjectList = new ArrayList<Idea>();
        detectedObjectsIdea.setValue(detectedObjectList);

        for(ComposedObject composedObject: mergedObjectList) {
            int objectId = composedObject.getObjectId();
            double objectCenter_x = composedObject.getCurrentFrameIndividualObject().getCenterPoint().x;
            double objectCenter_y = composedObject.getCurrentFrameIndividualObject().getCenterPoint().y;
            int object_height = composedObject.getCurrentFrameIndividualObject().getHeight();
            int object_width = composedObject.getCurrentFrameIndividualObject().getWidth();

            Idea objectIdea = new Idea("object "+objectId, "", 0);

            Idea centerPositionIdea = new Idea("center_position", "", 0);
            centerPositionIdea.add(new Idea("x", objectCenter_x));
            centerPositionIdea.add(new Idea("y", objectCenter_y));
            objectIdea.add(centerPositionIdea);

            Idea sizeIdea = new Idea ("shape", "", 0);
            sizeIdea.add(new Idea("height", object_height));
            sizeIdea.add(new Idea("width", object_width));
            objectIdea.add(sizeIdea);

            detectedObjectList.add(objectIdea);
        }
        return detectedObjectsIdea;
    }

    public BufferedImage buffImageFromIndividualObjectList(List<IndividualObject> individualObjectList) throws IOException {
        Mat drawing = Mat.zeros(new Size(304, 322), CvType.CV_8UC3);

        for (int i = 0; i < individualObjectList.size(); i++) {
            Scalar color = individualObjectList.get(i).getColor();
            Imgproc.rectangle(drawing,
                    individualObjectList.get(i).getBoundRect().tl(),
                    individualObjectList.get(i).getBoundRect().br(), color, 1);
        }

        BufferedImage bufferedImage = MatBufferedImageConverter.Mat2BufferedImage(drawing);

        return bufferedImage;
    }
}
