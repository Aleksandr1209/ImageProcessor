package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class ControlPanel extends JPanel {
    private ImageProcessorApp app;
    private ImageProcessor imageProcessor;
    private ImagePanel imagePanel;
    private JButton loadButton;
    private JButton sharpenButton;
    private JButton motionBlurButton;
    private JButton embossButton;
    private JButton medianFilterButton;
    private JButton cannyButton;
    private JButton robertsButton;
    private JFileChooser fileChooser;
    private JButton resetButton;

    public ControlPanel(ImageProcessorApp app, ImageProcessor imageProcessor, ImagePanel imagePanel) {
        this.app = app;
        this.imageProcessor = imageProcessor;
        this.imagePanel = imagePanel;

        setLayout(new FlowLayout());

        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "bmp", "gif"));

        loadButton = new JButton("Load Image");
        sharpenButton = new JButton("Sharpen");
        motionBlurButton = new JButton("Motion Blur");
        embossButton = new JButton("Emboss");
        medianFilterButton = new JButton("Median Filter");
        cannyButton = new JButton("Canny Edge");
        robertsButton = new JButton("Roberts Edge");
        resetButton = new JButton("Reset");

        add(loadButton);
        add(resetButton);
        add(sharpenButton);
        add(motionBlurButton);
        add(embossButton);
        add(medianFilterButton);
        add(cannyButton);
        add(robertsButton);

        setupButtonListeners();
    }

    private void setupButtonListeners() {
        loadButton.addActionListener(e -> {
            int returnVal = fileChooser.showOpenDialog(app);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    BufferedImage image = ImageIO.read(file);
                    imageProcessor.setOriginalImage(image);
                    imagePanel.setOriginalImage(image);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(app, "Error loading image: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        sharpenButton.addActionListener(e -> applyFilter("sharpen"));
        motionBlurButton.addActionListener(e -> applyFilter("motionBlur"));
        embossButton.addActionListener(e -> applyFilter("emboss"));
        medianFilterButton.addActionListener(e -> applyFilter("median"));
        cannyButton.addActionListener(e -> applyFilter("canny"));
        robertsButton.addActionListener(e -> applyFilter("roberts"));
        resetButton.addActionListener(e -> {
            if (imageProcessor.getOriginalImage() != null) {
                BufferedImage resetImage = imageProcessor.reset();
                imagePanel.setImage(resetImage);
            } else {
                JOptionPane.showMessageDialog(app, "No image to reset",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void applyFilter(String filterType) {
        if (imageProcessor.getOriginalImage() == null) {
            JOptionPane.showMessageDialog(app, "Please load an image first",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            BufferedImage result = null;
            switch (filterType) {
                case "sharpen":
                    result = imageProcessor.sharpen();
                    break;
                case "motionBlur":
                    result = imageProcessor.motionBlur();
                    break;
                case "emboss":
                    result = imageProcessor.emboss();
                    break;
                case "median":
                    result = imageProcessor.medianFilter();
                    break;
                case "canny":
                    result = imageProcessor.cannyEdgeDetection();
                    break;
                case "roberts":
                    result = imageProcessor.robertsEdgeDetection();
                    break;
            }
            imagePanel.setImage(result);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(app, "Error applying filter: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}