package org.example.visualization;

import javax.swing.*;
import java.awt.image.BufferedImage;

public interface JLabelImgUpdater {
    void updateJLabelImg(JLabel jLabelToUpdate, BufferedImage imgToSet);
}
