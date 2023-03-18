package org.example.mind.codelets;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_detection.ComposedObject;
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
            List<ComposedObject> composedObjectList = objectListProposer.getComposedObjectListFromCurrentFrame();
            BufferedImage objectsImg = buffImageFromObjectList(composedObjectList);
            updateJLabelImg(this.objectsImgJLabel, objectsImg);

            List<ComposedObject> mergedObjectList = objectListProposer.getComposedObjectMergedListFromCurrentFrame();
            BufferedImage mergedObjecImg = buffImageFromObjectList(mergedObjectList);
            updateJLabelImg(this.mergedObjectsImgJLabel, mergedObjecImg);
//          ------------------------------------


//            detectedObjectsMO.setI(objectIdea);
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
}
