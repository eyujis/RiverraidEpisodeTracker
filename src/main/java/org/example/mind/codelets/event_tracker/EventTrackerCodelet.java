package org.example.mind.codelets.event_tracker;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import org.example.util.MatBufferedImageConverter;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class EventTrackerCodelet extends Codelet {
    Memory objectsBufferMO;
    Memory eventCategoriesMO;
    Memory detectedEventsMO;

    EventTracker eventTracker = new EventTracker();
    JLabel eventImgJLabel;

    public EventTrackerCodelet(JLabel eventImgJLabel) {
        this.eventImgJLabel = eventImgJLabel;
    }

    @Override
    public void accessMemoryObjects() {
        objectsBufferMO=(MemoryObject)this.getInput("OBJECTS_BUFFER");
        eventCategoriesMO=(MemoryObject)this.getInput("EVENT_CATEGORIES");
        detectedEventsMO=(MemoryObject)this.getOutput("DETECTED_EVENTS");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        if(objectsBufferMO.getI()=="" || eventCategoriesMO.getI()=="") {
            return;
        }
        Idea objectsBuffer = (Idea) objectsBufferMO.getI();
        Idea eventCategories = (Idea) eventCategoriesMO.getI();
        eventTracker.detectEvents(objectsBuffer, eventCategories);

        for(Idea eventIdea: eventTracker.getDetectedEvents().getL()) {
            System.out.println(eventIdea.toStringFull());
        }

        try {
            updateJLabelImg(eventImgJLabel, getBuffImageFromEvents(eventTracker.getDetectedEvents()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        detectedEventsMO.setI(eventTracker.getDetectedEvents());
    }

    public void updateJLabelImg(JLabel jLabelToUpdate, BufferedImage imgToSet) {
        jLabelToUpdate.setIcon(new ImageIcon(imgToSet));
    }


    public BufferedImage getBuffImageFromEvents(Idea events) throws IOException {
        Mat frame = new Mat(new Size(304, 322), CvType.CV_8UC3, new Scalar(0,0,0));

        for(Idea eventIdea : events.getL()) {
            double x_start = (double) eventIdea.get("initialPosition.x").getValue();
            double y_start = (double) eventIdea.get("initialPosition.y").getValue();

            double[] event_vector =  (double[]) eventIdea.get("eventVector").getValue();
            double x_end = x_start + event_vector[0];
            double y_end = y_start + event_vector[1];

            Point start = new Point(x_start, y_start);
            Point end = new Point(x_end, y_end);
            Scalar color = new Scalar(255, 255, 255);

            int thickness = 1;

            Imgproc.arrowedLine(frame, start, end, color, thickness);
        }

        BufferedImage bufferedImage = MatBufferedImageConverter.Mat2BufferedImage(frame);

        return bufferedImage;
    }
}
