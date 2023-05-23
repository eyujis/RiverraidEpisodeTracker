package org.example.visualization;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MemoriesJFrame extends JFrame {
    String templateImagePath = "template_img.tiff";
    JLabel rawDataBufferImgJLabel;
    JLabel objectsImgJLabel;
    JLabel mergedObjectsImgJLabel;
    JLabel categoriesImgJLabel;

    public MemoriesJFrame() throws IOException {

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        rawDataBufferImgJLabel = jLabelWithTemplateImgIcon();
        objectsImgJLabel = jLabelWithTemplateImgIcon();
        mergedObjectsImgJLabel = jLabelWithTemplateImgIcon();
        categoriesImgJLabel = jLabelWithTemplateImgIcon();

        JPanel panel = new JPanel();
        panel.add(rawDataBufferImgJLabel);
        panel.add(objectsImgJLabel);
        panel.add(mergedObjectsImgJLabel);
        panel.add(categoriesImgJLabel);

        this.getContentPane().add(panel, BorderLayout.PAGE_END);

        this.pack();
    }

    public JLabel jLabelWithTemplateImgIcon() {
        JLabel imageLbl = new JLabel();
        imageLbl.setIcon(null);
        try {
            BufferedImage img= ImageIO.read(getClass().getClassLoader().getResource(templateImagePath));
            imageLbl.setIcon(new ImageIcon(img));
            imageLbl.revalidate();
            imageLbl.repaint();
            imageLbl.update(imageLbl.getGraphics());
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return imageLbl;
    }

    public JLabel getRawDataBufferImgJLabel() {
        return rawDataBufferImgJLabel;
    }

    public JLabel getObjectsImgJLabel() {
        return objectsImgJLabel;
    }

    public JLabel getMergedObjectsImgJLabel() {
        return mergedObjectsImgJLabel;
    }

    public JLabel getCategoriesImgJLabel() {
        return categoriesImgJLabel;
    }
}
