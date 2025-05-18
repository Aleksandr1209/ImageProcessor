package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageProcessorApp extends JFrame {
    private ImagePanel imagePanel;
    private ControlPanel controlPanel;
    private ImageProcessor imageProcessor;

    public ImageProcessorApp() {
        super("Image Processor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLayout(new BorderLayout());

        imageProcessor = new ImageProcessor();
        imagePanel = new ImagePanel();
        controlPanel = new ControlPanel(this, imageProcessor, imagePanel);

        add(imagePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    public void updateImage(BufferedImage image) {
        imagePanel.setImage(image);
    }
}