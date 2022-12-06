package org.example.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PerceptFrame extends JFrame {
    String path = "template_img.tiff";

    public PerceptFrame() throws IOException {

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JLabel socketImgLbl = jLabelWithTemplateImgIcon();
        JLabel contourImgLbl = jLabelWithTemplateImgIcon();
        JLabel contourConcatImgLbl = jLabelWithTemplateImgIcon();

        JButton button = new JButton("Start Capture");
        button.addActionListener(new ButtonStartRcvImg(socketImgLbl, contourImgLbl, contourConcatImgLbl));

        JPanel panel = new JPanel();
        panel.add(socketImgLbl);
        panel.add(contourImgLbl);
        panel.add(contourConcatImgLbl);


        this.getContentPane().add(button, BorderLayout.PAGE_START);
        this.getContentPane().add(panel, BorderLayout.PAGE_END);

        this.pack();
    }

    public JLabel jLabelWithTemplateImgIcon() {
        JLabel imageLbl = new JLabel();
        imageLbl.setIcon(null);
        try {
            BufferedImage img= ImageIO.read(getClass().getClassLoader().getResource(path));
            imageLbl.setIcon(new ImageIcon(img));
            imageLbl.revalidate();
            imageLbl.repaint();
            imageLbl.update(imageLbl.getGraphics());
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return imageLbl;
    }

}
