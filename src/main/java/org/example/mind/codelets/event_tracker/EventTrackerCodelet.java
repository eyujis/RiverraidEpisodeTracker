package org.example.mind.codelets.event_tracker;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import org.example.util.MatBufferedImageConverter;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

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
        Idea detectedEvents = null;

        if(detectedEventsMO.getI()=="") {
            detectedEvents = new Idea();
        } else {
            detectedEvents = (Idea) detectedEventsMO.getI();
        }

        eventTracker.detectEvents(objectsBuffer, eventCategories, detectedEvents);

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
            if (((String) eventIdea.get("eventCategory").getValue()).startsWith("VectorEventCategory")) {
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

            if(((String) eventIdea.get("eventCategory").getValue()).startsWith("AppearanceEventCategory")) {
                Idea positionIdea = null;
                Scalar color = null;

                if(eventIdea.get("appearPosition") != null) {
                    positionIdea = eventIdea.get("appearPosition");
                    color = new Scalar(0, 255, 0);
                }
                if(eventIdea.get("disappearPosition") != null) {
                    positionIdea = eventIdea.get("disappearPosition");
                    color = new Scalar(0, 0, 255);
                }

                double x = (double) positionIdea.get("x").getValue();
                double y = (double) positionIdea.get("y").getValue();


                Point position = new Point(x, y);
                int radius = 5;

                int thickness = 1;
                Imgproc.circle (
                        frame,                 //Matrix obj of the image
                        position,    //Center of the circle
                        radius,                    //Radius
                        color,  //Scalar object for color
                        thickness                      //Thickness of the circle
                );
            }
        }

        BufferedImage bufferedImage = MatBufferedImageConverter.Mat2BufferedImage(frame);

        return bufferedImage;
    }
}
