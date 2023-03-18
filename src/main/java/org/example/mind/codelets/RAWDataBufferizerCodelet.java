package org.example.mind.codelets;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import org.example.environment.RiverRaidEnv;
import org.example.visualization.JLabelImgUpdater;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RAWDataBufferizerCodelet extends Codelet implements JLabelImgUpdater {
    private RiverRaidEnv env;
    private Memory rawDataBufferMO;
    private final int bufferSize = 2;
    private Idea ideaBuffer = initializeIdeaBuffer(bufferSize);
    private JLabel rawDataBufferImgJLabel;


    public RAWDataBufferizerCodelet(RiverRaidEnv env, JLabel rawDataBufferImgJLabel) {
        this.env = env;
        this.rawDataBufferImgJLabel = rawDataBufferImgJLabel;
    }

    @Override
    public void accessMemoryObjects() {
        rawDataBufferMO=(MemoryObject)this.getOutput("RAW_DATA_BUFFER");
    }

    @Override
    public void proc() {
        BufferedImage frame = null;
        try {
            frame = this.env.step();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Idea> frames = (List<Idea>) ideaBuffer.get("frames").getValue();

        addFrame(frames, frame, bufferSize);

        rawDataBufferMO.setI((Idea) ideaBuffer);

//        Visualization
        Idea rawDataBufferIdea = (Idea) rawDataBufferMO.getI();
        List<Idea> framesIdea = (List<Idea>) rawDataBufferIdea.get("frames").getValue();
        BufferedImage imageToUpdate = (BufferedImage) (framesIdea.get(0).getValue());
        updateJLabelImg(this.rawDataBufferImgJLabel, imageToUpdate);
    }

    @Override
    public void calculateActivation() {

    }

    public Idea initializeIdeaBuffer(int bufferSize) {
        Idea ideaBuffer = new Idea("buffer","",0);

        List<Idea> frames = new ArrayList<Idea>();
        for(int i=0; i<bufferSize; i++)    {
            Idea frame = new Idea("frame", null);
            frames.add(frame);
        }

        ideaBuffer.add(new Idea("frames",frames));
        ideaBuffer.add(new Idea("bufferSize", bufferSize));

        return ideaBuffer;
    }

    private void addFrame(List<Idea> frames, BufferedImage currentFrame, int bufferSize) {

        // shift right position from frames i=buffer_size-1 to i=0
        for(int i=bufferSize-2; i>=0; i--)    {
            // get ith values
            BufferedImage ithTimestampFrame = (BufferedImage) frames.get(i).getValue();
            // set i+1th values
            frames.get(i+1).setValue(ithTimestampFrame);
        }
        frames.get(0).setValue(currentFrame);

    }

    @Override
    public void updateJLabelImg(JLabel jLabelToUpdate, BufferedImage imgToUpdate) {
        jLabelToUpdate.setIcon(new ImageIcon(imgToUpdate));
    }
}
