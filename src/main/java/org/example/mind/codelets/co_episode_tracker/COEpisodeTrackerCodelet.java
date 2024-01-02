package org.example.mind.codelets.co_episode_tracker;

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
import java.util.Optional;

public class COEpisodeTrackerCodelet extends Codelet {
    Memory sOEpisodesMO;
    Memory cOEpisodeCategoriesMO;
    Memory detectedCOEpisodesMO;
    Memory cOEpisodeTrackerTSMO;

    JLabel coEpisodeImgJLabel;
    Category2Color category2Color = new Category2Color();

    public COEpisodeTrackerCodelet(JLabel coEpisodeImgJLabel) {
        this.coEpisodeImgJLabel = coEpisodeImgJLabel;
    }

    @Override
    public void accessMemoryObjects() {
        sOEpisodesMO=(MemoryObject)this.getInput("DETECTED_EVENTS");
        cOEpisodeCategoriesMO=(MemoryObject)this.getInput("CO_EPISODE_CATEGORIES");
        detectedCOEpisodesMO=(MemoryObject)this.getOutput("DETECTED_CO_EPISODES");
        cOEpisodeTrackerTSMO =(MemoryObject)this.getOutput("CO_EPISODES_TS");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        if(sOEpisodesMO.getI()=="" || cOEpisodeCategoriesMO.getI()=="") {
            return;
        }

        if(detectedCOEpisodesMO.getI() == "") {
            detectedCOEpisodesMO.setI(new Idea("COEpisodes", "", 0));
            return;
        }

        if(cOEpisodeTrackerTSMO.getI()=="") {
            cOEpisodeTrackerTSMO.setI(-1);
        }

        Idea sOEpisodes = (Idea) sOEpisodesMO.getI();
        Idea cOEpisodeCategories = (Idea) cOEpisodeCategoriesMO.getI();
        Idea previousCOEpisodes = (Idea) detectedCOEpisodesMO.getI();

        int lastTimestamp = (int) cOEpisodeTrackerTSMO.getI();

        synchronized (sOEpisodesMO) {
            synchronized (cOEpisodeCategoriesMO) {
                synchronized (detectedCOEpisodesMO) {
                    int currentTimestamp = (int) sOEpisodes.getValue();

                    if(lastTimestamp==currentTimestamp) {
                        return;
                    } else {
                        cOEpisodeTrackerTSMO.setI(currentTimestamp);
                    }

                    Idea cOEpisodes = new COEpisodeTracker().updateRelations(sOEpisodes,
                            cOEpisodeCategories,
                            previousCOEpisodes);

                    detectedCOEpisodesMO.setI(cOEpisodes);

                    try {
                        updateJLabelImg(coEpisodeImgJLabel, getBuffImageFromEvents(cOEpisodes));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public void updateJLabelImg(JLabel jLabelToUpdate, BufferedImage imgToSet) {
        jLabelToUpdate.setIcon(new ImageIcon(imgToSet));
    }


    public BufferedImage getBuffImageFromEvents(Idea events) throws IOException {
        Mat frame = new Mat(new Size(304, 322), CvType.CV_8UC3, new Scalar(0,0,0));

        for(Idea eventIdea : events.getL()) {
            if(((String) eventIdea.get("eventCategory").getValue()).startsWith("VectorEventCategory")) {
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
            }

            if(((String) eventIdea.get("eventCategory").getValue()).startsWith("AppearanceEventCategory")) {
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
            }

            Idea relations = eventIdea.get("relations");
            for(Idea relation : relations.getL()) {
                if(relation.get("relationType").getValue() == "m") {
                    int rEventId = (int) relation.get("eventId").getValue();
                    Optional<Idea> optionalREvent = events.getL().stream().filter(event -> ((int) event.get("eventId").getValue()) == rEventId).findFirst();
                    if (optionalREvent.isPresent()) {
                        Idea rEvent = optionalREvent.get();

                        double x_start = 0;
                        double y_start = 0;

                        double x_end = 0;
                        double y_end = 0;

                        if(((String) eventIdea.get("eventCategory").getValue()).startsWith("VectorEventCategory")) {
                            double x_start_vec = (double) eventIdea.get("initialPropertyState.x").getValue();
                            double y_start_vec = (double) eventIdea.get("initialPropertyState.y").getValue();

                            double[] event_vector =  (double[]) eventIdea.get("eventVector").getValue();
                            x_start = x_start_vec + event_vector[0];
                            y_start = y_start_vec + event_vector[1];
                        } else {
                            x_start = (double) eventIdea.get("lastObjectState.center.x").getValue();
                            y_start = (double) eventIdea.get("lastObjectState.center.y").getValue();
                        }

                        if(((String) rEvent.get("eventCategory").getValue()).startsWith("VectorEventCategory")) {
                            x_end = (double) rEvent.get("initialPropertyState.x").getValue();
                            y_end = (double) rEvent.get("initialPropertyState.y").getValue();

                        } else {
                            x_end = (double) rEvent.get("lastObjectState.center.x").getValue();
                            y_end = (double) rEvent.get("lastObjectState.center.y").getValue();
                        }


                        Point start = new Point(x_start, y_start);
                        Point end = new Point(x_end, y_end);

                        Imgproc.arrowedLine(frame, start, end, new Scalar(255,255,255), 1);

                    }
                }
            }
        }

        BufferedImage bufferedImage = MatBufferedImageConverter.Mat2BufferedImage(frame);

        return bufferedImage;
    }
}
