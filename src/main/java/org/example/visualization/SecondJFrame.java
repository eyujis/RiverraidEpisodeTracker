package org.example.visualization;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SecondJFrame extends JFrame {
    String templateImagePath = "template_img.tiff";
    JLabel eventTrackerImgJLabel;
    JLabel forgettingSOEpisodesImgJLabel;
    JLabel cOEpisodesImgJLabel;

    public SecondJFrame() throws IOException {

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        eventTrackerImgJLabel = jLabelWithTemplateImgIcon();
        forgettingSOEpisodesImgJLabel = jLabelWithTemplateImgIcon();
        cOEpisodesImgJLabel = jLabelWithTemplateImgIcon();

        JPanel panel = new JPanel();
        panel.add(eventTrackerImgJLabel);
        panel.add(forgettingSOEpisodesImgJLabel);
        panel.add(cOEpisodesImgJLabel);

        this.getContentPane().add(panel, BorderLayout.PAGE_END);

        this.pack();
    }

    public JLabel jLabelWithTemplateImgIcon() {
        JLabel imageLbl = new JLabel();
        imageLbl.setIcon(null);
        try {
            BufferedImage img= ImageIO.read(getClass().getClassLoader().getResource(templateImagePath));
            imageLbl.setIcon(new ImageIcon(img));
//            imageLbl.revalidate();
//            imageLbl.repaint();
//            imageLbl.update(imageLbl.getGraphics());
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return imageLbl;
    }

    public JLabel getEventTrackerImgJLabel() {
        return eventTrackerImgJLabel;
    }

    public JLabel getForgettingSOEpisodesImgJLabel() {
        return forgettingSOEpisodesImgJLabel;
    }

    public JLabel getcOEpisodesImgJLabel() {
        return cOEpisodesImgJLabel;
    }
}
