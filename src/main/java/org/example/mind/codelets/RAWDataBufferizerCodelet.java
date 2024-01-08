package org.example.mind.codelets;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import org.example.environment.RiverRaidEnv;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class RAWDataBufferizerCodelet extends Codelet {
    private RiverRaidEnv env;
    private Memory rawDataBufferMO;
    private final int BUFFER_SIZE = 2;
//    private Idea ideaBuffer = initializeIdeaBuffer();
    private JLabel rawDataBufferImgJLabel;
    Idea rawDataBuffer = new Idea("rawDataBuffer", "", 0);


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
        BufferedImage image = null;
        try {
            image = this.env.step();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int timestamp = this.env.getNStep();

        addElement(image, timestamp);

        if(rawDataBuffer.getL().size()>=BUFFER_SIZE) {
            rawDataBufferMO.setI(rawDataBuffer);
        }


//        Visualization
        if(rawDataBufferMO.getI() instanceof Idea) {
            Idea rawDataBufferIdea = (Idea) rawDataBufferMO.getI();
            BufferedImage imageToUpdate = (BufferedImage) rawDataBufferIdea.getL().get(0).get("image").getValue();;
            updateJLabelImg(this.rawDataBufferImgJLabel, imageToUpdate);
        }
    }

    @Override
    public void calculateActivation() {

    }

    public void addElement(BufferedImage image, int timestamp) {
        if(rawDataBuffer.getL().size()>=BUFFER_SIZE) {
            rawDataBuffer.getL().remove(0);
        }

        Idea rawData = new Idea("rawData", "", 0);
        rawData.add(new Idea("image", image));
        rawData.add(new Idea("timestamp", timestamp));

        rawDataBuffer.getL().add(rawData);
    }

    public void updateJLabelImg(JLabel jLabelToUpdate, BufferedImage imgToUpdate) {
        jLabelToUpdate.setIcon(new ImageIcon(imgToUpdate));
        jLabelToUpdate.revalidate();
        jLabelToUpdate.repaint();
        jLabelToUpdate.update(jLabelToUpdate.getGraphics());
    }
}
