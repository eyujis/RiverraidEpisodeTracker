package org.example.mind.codelets.event_tracker;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import org.example.util.MatBufferedImageConverter;
import org.example.visualization.Category2Color;
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
    Category2Color category2Color = new Category2Color();

    public EventTrackerCodelet(JLabel eventImgJLabel) {
        this.eventImgJLabel = eventImgJLabel;
    }

    @Override
    public void accessMemoryObjects() {
        objectsBufferMO=(MemoryObject)this.getInput("OBJECTS_BUFFER");
        eventCategoriesMO=(MemoryObject)this.getOutput("EVENT_CATEGORIES");
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

        synchronized (eventCategoriesMO) {
            Idea objectsBuffer = (Idea) objectsBufferMO.getI();
            Idea eventCategories = (Idea) eventCategoriesMO.getI();
            Idea detectedEvents = null;

            if(detectedEventsMO.getI()=="") {
                detectedEvents = new Idea();
            } else {
                detectedEvents = (Idea) detectedEventsMO.getI();
            }

            eventTracker.detectEvents(objectsBuffer, eventCategories, detectedEvents);
            detectedEventsMO.setI(eventTracker.getDetectedEvents());

            eventCategories.getL().addAll(eventTracker.getAssimilatedCategories().getL());
            eventCategoriesMO.setI(eventCategories);
        }
        try {
            updateJLabelImg(eventImgJLabel, getBuffImageFromEvents(eventTracker.getDetectedEvents()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateJLabelImg(JLabel jLabelToUpdate, BufferedImage imgToSet) {
        jLabelToUpdate.setIcon(new ImageIcon(imgToSet));
        jLabelToUpdate.revalidate();
        jLabelToUpdate.repaint();
        jLabelToUpdate.update(jLabelToUpdate.getGraphics());
    }


    public BufferedImage getBuffImageFromEvents(Idea events) throws IOException {
//        Mat frame = new Mat(new Size(304, 322), CvType.CV_8UC3, new Scalar(0,0,0));
        Mat frame = new Mat(new Size(304, 364), CvType.CV_8UC3, new Scalar(100,100,100));

        addTimestamp(frame, events.getValue().toString());

        for(Idea eventIdea : events.getL()) {
            if(((String) eventIdea.get("eventCategory").getValue()).startsWith("VectorEventCategory")
            && ((boolean) eventIdea.get("hasFinished").getValue())==false) {
                double x_start = (double) eventIdea.get("initialPropertyState.x").getValue();
                double y_start = (double) eventIdea.get("initialPropertyState.y").getValue();

                double[] event_vector =  (double[]) eventIdea.get("eventVector").getValue();
                double x_end = x_start + event_vector[0];
                double y_end = y_start + event_vector[1];

                String eventCategory = (String) eventIdea.get("eventCategory").getValue();

                Point start = new Point(x_start, y_start);
                Point end = new Point(x_end, y_end);
                Scalar color = category2Color.getColor(eventCategory);
                int thickness = 2;
                Imgproc.arrowedLine(frame, start, end, color, thickness);

                addTextId(frame, eventIdea, new Point((x_start+x_end)/2, (y_start+y_end)/2), color);
            }

            if(((String) eventIdea.get("eventCategory").getValue()).startsWith("AppearanceEventCategory")
                    && ((boolean) eventIdea.get("hasFinished").getValue())==false) {
                Idea positionIdea = null;
                Scalar color = null;

                if(eventIdea.get("appearanceEventType").getValue() != "disappear") {
                    positionIdea = eventIdea.get("lastObjectState.center");
                    color = new Scalar(0, 255, 0);
                }
                if(eventIdea.get("appearanceEventType").getValue() != "appear") {
                    positionIdea = eventIdea.get("lastObjectState.center");
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

                addTextId(frame, eventIdea, position, color);
            }
        }

        BufferedImage bufferedImage = MatBufferedImageConverter.Mat2BufferedImage(frame);

        return bufferedImage;
    }

    private void addTextId(Mat frame, Idea eventIdea, Point point, Scalar color) {
        String text = eventIdea.get("eventId").getValue().toString();
        int fontFace = Imgproc.FONT_HERSHEY_SIMPLEX;
        double fontScale = 0.5;
        int textThickness = 1;
        Imgproc.putText(frame, text, point, fontFace, fontScale, color, textThickness);
    }

    private void addTimestamp(Mat frame, String timestamp) {
        int fontFace = Imgproc.FONT_HERSHEY_SIMPLEX;
        double fontScale = 0.5;
        int textThickness = 1;
        Imgproc.putText(frame, String.valueOf(timestamp), new Point(10,20), fontFace, fontScale, new Scalar(255,255,255), textThickness);
    }
}
