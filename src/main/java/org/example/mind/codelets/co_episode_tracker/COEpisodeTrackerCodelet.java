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
import javax.swing.text.html.Option;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

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
        cOEpisodeCategoriesMO=(MemoryObject)this.getOutput("CO_EPISODE_CATEGORIES");
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

                    updateJLabelImg(coEpisodeImgJLabel, getBuffImageFromEvents(cOEpisodes));
                }
            }
        }
    }

    public void updateJLabelImg(JLabel jLabelToUpdate, BufferedImage imgToSet) {
        jLabelToUpdate.setIcon(new ImageIcon(imgToSet));
        jLabelToUpdate.revalidate();
        jLabelToUpdate.repaint();
        jLabelToUpdate.update(jLabelToUpdate.getGraphics());
    }


    public BufferedImage getBuffImageFromEvents(Idea events) {
        Mat frame = new Mat(new Size(304, 322), CvType.CV_8UC3, new Scalar(0,0,0));

        addTimestamp(frame, events.getValue().toString());
        System.out.println("==========="+ events.getValue().toString() +"===========");

        for(Idea eventIdea : events.getL()) {
            ArrayList<Idea> inverseMeetsRelations = (ArrayList<Idea>) eventIdea.get("relations").getL().stream()
                    .filter(event -> ((String) event.get("relationType").getValue()).equals("mi")).collect(Collectors.toList());

            boolean isRootRelation = inverseMeetsRelations.isEmpty();
            boolean inverseRelationsNotFoundInFrame = false;

            if(!isRootRelation && !((boolean) eventIdea.get("hasFinished").getValue())) {
                ArrayList<Integer> inverseMeetsRelationsIds = (ArrayList<Integer>) inverseMeetsRelations.stream()
                        .map(relation -> (Integer) relation.get("eventId").getValue())
                        .collect(Collectors.toList());

                ArrayList<Integer> eventIds = (ArrayList<Integer>) events.getL().stream()
                        .map(event -> (Integer) event.get("eventId").getValue())
                        .collect(Collectors.toList());

                for(Integer relationId : inverseMeetsRelationsIds) {
                    if(!eventIds.contains(relationId)) {
                        inverseRelationsNotFoundInFrame=true;
                    }
                }
            }

            if (isRootRelation) {
                Point position = null;

                if(((String) eventIdea.get("eventCategory").getValue()).startsWith("VectorEventCategory")) {
                    double x_start = (double) eventIdea.get("initialPropertyState.x").getValue();
                    double y_start = (double) eventIdea.get("initialPropertyState.y").getValue();

                    position = new Point(x_start, y_start);
                }

                if(((String) eventIdea.get("eventCategory").getValue()).startsWith("AppearanceEventCategory")) {

                    Idea positionIdea = eventIdea.get("lastObjectState.center");
                    double x = (double) positionIdea.get("x").getValue();
                    double y = (double) positionIdea.get("y").getValue();

                    position = new Point(x, y);
                }

                Imgproc.circle (
                        frame,                 //Matrix obj of the image
                        position,    //Center of the circle
                        3,                    //Radius
                        new Scalar(255,255,255),  //Scalar object for color
                        1                      //Thickness of the circle
                );

                addTextId(frame, eventIdea, position, new Scalar(255,255,255));
                String causalChain = "Source: " + eventIdea.get("eventId").getValue().toString();

                causalChain = causalChain + drawRelations(frame, position, eventIdea.get("relations"), events);

            }

            else if(inverseRelationsNotFoundInFrame && !((boolean) eventIdea.get("hasFinished").getValue())) {
                Point position = null;

                if(((String) eventIdea.get("eventCategory").getValue()).startsWith("VectorEventCategory")) {
                    double x_start = (double) eventIdea.get("initialPropertyState.x").getValue();
                    double y_start = (double) eventIdea.get("initialPropertyState.y").getValue();


                    double[] event_vector =  (double[]) eventIdea.get("eventVector").getValue();
                    double x_end = x_start + event_vector[0];
                    double y_end = y_start + event_vector[1];

                    Point origin = new Point(x_start, y_start);
                    position = new Point(x_end, y_end);

                    Imgproc.arrowedLine(frame, origin, position, new Scalar(255,255,255), 1);
                }

                if(((String) eventIdea.get("eventCategory").getValue()).startsWith("AppearanceEventCategory")) {

                    Idea positionIdea = eventIdea.get("lastObjectState.center");
                    double x = (double) positionIdea.get("x").getValue();
                    double y = (double) positionIdea.get("y").getValue();

                    position = new Point(x, y);
                    Imgproc.circle (
                            frame,                 //Matrix obj of the image
                            position,    //Center of the circle
                            3,                    //Radius
                            new Scalar(255,255,255),  //Scalar object for color
                            1                      //Thickness of the circle
                    );
                }
                addTextId(frame, eventIdea, position, new Scalar(255,255,255));
                drawRelations(frame, position, eventIdea.get("relations"), events);

            }

        }

        BufferedImage bufferedImage = null;
        try {
            bufferedImage = MatBufferedImageConverter.Mat2BufferedImage(frame);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return bufferedImage;
    }

    private String drawRelations(Mat frame, Point origin, Idea relations, Idea events) {
        String result = "\n \t";

        for (Idea relation: relations.getL()) {
            String relationType = (String) relation.get("relationType").getValue();
            if(relationType.equals("m")) {
                int rEventId = (int) relation.get("eventId").getValue();
                Optional<Idea> optionalREvent = events.getL().stream()
                        .filter(event -> ((int) event.get("eventId").getValue()) == rEventId)
                        .findFirst();

                if (optionalREvent.isPresent()) {
                    Idea rEvent = optionalREvent.get();
                    double x_end = 0;
                    double y_end = 0;

                    if(((String) rEvent.get("eventCategory").getValue()).startsWith("VectorEventCategory")) {
                        double x_start_vec = (double) rEvent.get("initialPropertyState.x").getValue();
                        double y_start_vec = (double) rEvent.get("initialPropertyState.y").getValue();

                        double[] event_vector =  (double[]) rEvent.get("eventVector").getValue();
                        x_end = x_start_vec + event_vector[0];
                        y_end = y_start_vec + event_vector[1];

                    } else {
                        x_end = (double) rEvent.get("lastObjectState.center.x").getValue();
                        y_end = (double) rEvent.get("lastObjectState.center.y").getValue();
                    }

                    Point end = new Point(x_end, y_end);

                    Imgproc.arrowedLine(frame, origin, end, new Scalar(255,255,255), 1);
                    addTextId(frame, rEvent, end, new Scalar(255,255,255));

                    result = result + rEvent.get("eventId").getValue().toString() + "\n";

                    return result + drawRelations(frame, end, rEvent.get("relations"), events);
                }
            }
        }

        return "";
    }

    private void addTextId(Mat frame, Idea eventIdea, Point point, Scalar color) {
        String text = eventIdea.get("eventId").getValue().toString();
        int fontFace = Imgproc.FONT_HERSHEY_SIMPLEX;
        double fontScale = 0.5;
        int textThickness = 1;
//        Imgproc.putText(frame, text, point, fontFace, fontScale, color, textThickness);
    }

    private void addTimestamp(Mat frame, String timestamp) {
        int fontFace = Imgproc.FONT_HERSHEY_SIMPLEX;
        double fontScale = 0.5;
        int textThickness = 1;
        Imgproc.putText(frame, String.valueOf(timestamp), new Point(10,20), fontFace, fontScale, new Scalar(255,255,255), textThickness);
    }

}
