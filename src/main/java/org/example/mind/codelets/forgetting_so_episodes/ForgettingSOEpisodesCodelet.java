package org.example.mind.codelets.forgetting_so_episodes;

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
import java.security.cert.PolicyNode;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ForgettingSOEpisodesCodelet extends Codelet {
    // in timestamp units
    int FORGETTING_THRESHOLD = 2;

    Memory sOEpisodesMO;
    int currentTimestamp;

    JLabel forgettingSOEpisodesImgJLabel;

    Category2Color category2Color = new Category2Color();

    public ForgettingSOEpisodesCodelet(JLabel forgettingSOEpisodesImgJLabel) {
        this.forgettingSOEpisodesImgJLabel = forgettingSOEpisodesImgJLabel;
    }

    @Override
    public void accessMemoryObjects() {
        sOEpisodesMO=(MemoryObject)this.getOutput("DETECTED_EVENTS");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        synchronized(sOEpisodesMO) {
            if(sOEpisodesMO.getI()!="") {
                Idea unfilteredSOEpisodes = (Idea) sOEpisodesMO.getI();
                currentTimestamp = (int) unfilteredSOEpisodes.getValue();

                Idea filteredSOEpisodes = new Idea("FilteredSOEpisodes", currentTimestamp, 0);

                ArrayList<Idea> filteredSOEpisodesList = (ArrayList<Idea>) unfilteredSOEpisodes.getL()
                        .stream().filter(event-> !isForget(event))
                        .collect(Collectors.toList());

                filteredSOEpisodes.getL().addAll(filteredSOEpisodesList);
                sOEpisodesMO.setI(filteredSOEpisodes);

                System.out.println("============="+filteredSOEpisodes.getValue()+"=============");
                for(Idea filteredSOEpisode : filteredSOEpisodes.getL()) {
                    System.out.println(filteredSOEpisode.toStringFull());
                }

                if(forgettingSOEpisodesImgJLabel!=null) {
                    try {
                        updateJLabelImg(forgettingSOEpisodesImgJLabel, getBuffImageFromEvents(filteredSOEpisodes));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

    }

    private boolean isForget(Idea event) {
        int lastEventTimestamp = (int) event.get("currentTimestamp").getValue();
        boolean hasFinished = (boolean) event.get("hasFinished").getValue();

        if(hasFinished && currentTimestamp>(lastEventTimestamp+FORGETTING_THRESHOLD)) {
            return true;
        }
        return false;
    }

    public void updateJLabelImg(JLabel jLabelToUpdate, BufferedImage imgToSet) {
        jLabelToUpdate.setIcon(new ImageIcon(imgToSet));
        jLabelToUpdate.revalidate();
        jLabelToUpdate.repaint();
        jLabelToUpdate.update(jLabelToUpdate.getGraphics());
    }


    public BufferedImage getBuffImageFromEvents(Idea events) throws IOException {
//        Mat frame = new Mat(new Size(304, 322), CvType.CV_8UC3, new Scalar(0,0,0));
        Mat frame = new Mat(new Size(304, 364), CvType.CV_8UC3, new Scalar(0,0,0));

        addTimestamp(frame, events.getValue().toString());

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

                addTextId(frame, eventIdea, new Point((x_start+x_end)/2, (y_start+y_end)/2), color);

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
