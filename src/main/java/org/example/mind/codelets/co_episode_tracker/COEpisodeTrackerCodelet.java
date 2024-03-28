package org.example.mind.codelets.co_episode_tracker;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import org.example.util.MatBufferedImageConverter;
import org.example.visualization.Category2Color;
import org.example.visualization.Relation2Color;
import org.example.visualization.RelationMToRelationMI;
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

    Relation2Color relation2Color = new Relation2Color();
    RelationMToRelationMI relationMToRelationMI = new RelationMToRelationMI();


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

                    //Visualization
                    System.out.println("==========="+ cOEpisodes.getValue().toString() +"===========");
                    for(Idea cOEpisode : cOEpisodes.getL()) {
//                   System.out.println(cOEpisode.toStringFull());
                        if(cOEpisode.get("relations").getL().stream()
                                .filter(relation->relation.get("relationType").getValue().equals("m")).collect(Collectors.toList()).size()>1) {
                            System.out.println(cOEpisode.get("eventId").getValue() +": "+ cOEpisode.get("relations").getL().stream().map(relation-> ((String) relation.get("relationType").getValue() + ((Integer) relation.get("eventId").getValue()).toString())).collect(Collectors.toList()));
                        }
                    }

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

        for(Idea eventIdea : events.getL()) {
            if(((String) eventIdea.get("eventCategory").getValue()).startsWith("VectorEventCategory")
//                    && ((boolean) eventIdea.get("hasFinished").getValue())==false
            ) {
                double x_start = (double) eventIdea.get("initialPropertyState.x").getValue();
                double y_start = (double) eventIdea.get("initialPropertyState.y").getValue();

                double[] event_vector =  (double[]) eventIdea.get("eventVector").getValue();
                double x_end = x_start + event_vector[0];
                double y_end = y_start + event_vector[1];

                String eventCategory = (String) eventIdea.get("eventCategory").getValue();

                Point start = new Point(x_start, y_start);
                Point end = new Point(x_end, y_end);



                Scalar color = findColor(eventIdea);
                int thickness = 2;
                Imgproc.arrowedLine(frame, start, end, color, thickness, 8, 0, 5/Math.max(0.1, pointDistance(start, end)));

                addTextId(frame, eventIdea, new Point((x_start+x_end)/2, (y_start+y_end)/2), color);
            }

            if(((String) eventIdea.get("eventCategory").getValue()).startsWith("AppearanceEventCategory")
//                    && ((boolean) eventIdea.get("hasFinished").getValue())==false
            ) {
                Idea positionIdea = null;
                Scalar color = null;

                if(eventIdea.get("appearanceEventType").getValue() != "disappear") {
                    positionIdea = eventIdea.get("lastObjectState.center");
                    color = findColor(eventIdea);
                }
                if(eventIdea.get("appearanceEventType").getValue() != "appear") {
                    positionIdea = eventIdea.get("lastObjectState.center");
                    color = findColor(eventIdea);
                }

                double x = (double) positionIdea.get("x").getValue();
                double y = (double) positionIdea.get("y").getValue();


                Point position = new Point(x, y);
                int radius = 5;

                int thickness = 2;
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

        BufferedImage bufferedImage = null;
        try {
            bufferedImage = MatBufferedImageConverter.Mat2BufferedImage(frame);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return bufferedImage;
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
        Imgproc.putText(frame, String.valueOf(timestamp), new Point(10,20), fontFace, fontScale,
                new Scalar(255,255,255), textThickness);
    }

    public Scalar findColor(Idea eventIdea) {
        int relationSize = eventIdea.get("relations").getL().size();
        if(relationSize==0) {
            return new Scalar(255, 255, 255);
        }

        Optional<Idea> relationMi = eventIdea.get("relations").getL().stream()
                .filter(relation -> relation.get("relationType").getValue().equals("mi")).findFirst();

        Scalar relationColor = null;
        if(relationMi.isPresent()) {
            relationMToRelationMI.putRelationIds((Integer) eventIdea.get("eventId").getValue(), (Integer) relationMi.get().get("eventId").getValue());
            relationColor = relation2Color.getColor(relationMToRelationMI.getRootMIId((Integer) eventIdea.get("eventId").getValue()));
        } else {
            relationColor = relation2Color.getColor(((Integer) eventIdea.get("eventId").getValue()));
        }

        return relationColor;
    }

    public double pointDistance(Point p1, Point p2) {
        double xSquared = Math.pow(p1.x-p2.x,2);
        double ySquared = Math.pow(p1.y-p2.y,2);
        return (Math.sqrt(xSquared+ySquared));
    }
}
