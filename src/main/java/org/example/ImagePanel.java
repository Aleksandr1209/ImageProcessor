package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {
    private BufferedImage originalImage;
    private BufferedImage processedImage;

    public ImagePanel() {
        setLayout(new GridLayout(1, 2));
    }

    public void setImage(BufferedImage image) {
        this.processedImage = image;
        repaint();
    }

    public void setOriginalImage(BufferedImage image) {
        this.originalImage = image;
        this.processedImage = image;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (originalImage != null && processedImage != null) {
            int panelWidth = getWidth() / 2;
            int panelHeight = getHeight();

            // Draw original image
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            double scale = Math.min((double)panelWidth / originalWidth,
                    (double)panelHeight / originalHeight);
            int scaledWidth = (int)(originalWidth * scale);
            int scaledHeight = (int)(originalHeight * scale);
            g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, this);

            // Draw processed image
            int processedWidth = processedImage.getWidth();
            int processedHeight = processedImage.getHeight();
            scale = Math.min((double)panelWidth / processedWidth,
                    (double)panelHeight / processedHeight);
            scaledWidth = (int)(processedWidth * scale);
            scaledHeight = (int)(processedHeight * scale);
            g.drawImage(processedImage, panelWidth, 0, scaledWidth, scaledHeight, this);

            // Draw labels
            g.setColor(Color.BLACK);
            g.drawString("Original Image", 10, 20);
            g.drawString("Processed Image", panelWidth + 10, 20);
        }
    }
}