package org.example.mind.codelets;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import org.example.environment.RiverRaidEnv;
import org.example.environment.RiverRaidPyGame;
import org.example.util.RawEnvInput;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class RAWDataBufferizerCodelet extends Codelet {
    private RiverRaidEnv env;
    private Memory imageBufferMO;
    private Memory rewardBufferMO;
    private Memory terminalBufferMO;
    private final int BUFFER_SIZE = 2;
    private final int MINIMUM_IMAGES_FOR_REGULAR_UPDATE = 3;
    private int totalImages = 0;
//    private Idea ideaBuffer = initializeIdeaBuffer();
    private JLabel rawDataBufferImgJLabel;
    Idea imageBuffer = new Idea("rawImageBuffer", "", 0);


    public RAWDataBufferizerCodelet(RiverRaidEnv env, JLabel rawDataBufferImgJLabel) {
        this.env = env;
        this.rawDataBufferImgJLabel = rawDataBufferImgJLabel;
    }

    @Override
    public void accessMemoryObjects() {
        imageBufferMO=(MemoryObject)this.getOutput("IMAGE_BUFFER");
        rewardBufferMO=(MemoryObject)this.getOutput("REWARD_BUFFER");
        terminalBufferMO=(MemoryObject)this.getOutput("TERMINAL_BUFFER");
    }

    @Override
    public void proc() {
        do {
            RawEnvInput input = this.env.step();
            addElement(input.bufferedImage, this.env.getNStep());

            if (imageBuffer.getL().size() >= BUFFER_SIZE) {
                imageBufferMO.setI(imageBuffer);
            }
            rewardBufferMO.setI(input.reward);
            terminalBufferMO.setI(input.terminal);

            //        Visualization
            if (imageBufferMO.getI() instanceof Idea && rawDataBufferImgJLabel != null) {
                Idea rawDataBufferIdea = (Idea) imageBufferMO.getI();
                BufferedImage imageToUpdate = (BufferedImage) rawDataBufferIdea.getL().get(0).get("image").getValue();
                updateJLabelImg(this.rawDataBufferImgJLabel, imageToUpdate);
            }

            totalImages += 1;
        } while (totalImages < MINIMUM_IMAGES_FOR_REGULAR_UPDATE);
    }

    @Override
    public void calculateActivation() {

    }

    public void addElement(BufferedImage image, int timestamp) {
        if(imageBuffer.getL().size()>=BUFFER_SIZE) {
            imageBuffer.getL().remove(0);
        }

        Idea rawData = new Idea("rawData", "", 0);
        rawData.add(new Idea("image", image));
        rawData.add(new Idea("timestamp", timestamp));

        imageBuffer.getL().add(rawData);
    }

    public void updateJLabelImg(JLabel jLabelToUpdate, BufferedImage imgToUpdate) {
        jLabelToUpdate.setIcon(new ImageIcon(imgToUpdate));
        jLabelToUpdate.revalidate();
        jLabelToUpdate.repaint();
        jLabelToUpdate.update(jLabelToUpdate.getGraphics());
    }
}
