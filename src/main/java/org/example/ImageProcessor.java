package org.example;

import java.awt.image.*;
import java.awt.*;

public class ImageProcessor {
    private BufferedImage originalImage;

    public void setOriginalImage(BufferedImage image) {
        this.originalImage = image;
    }

    public BufferedImage getOriginalImage() {
        return originalImage;
    }

    public BufferedImage sharpen() {
        if (originalImage == null) return null;

        float[] sharpenMatrix = {
                0, -1, 0,
                -1, 5, -1,
                0, -1, 0
        };

        return applyConvolutionFilter(new Kernel(3, 3, sharpenMatrix));
    }

    public BufferedImage motionBlur() {
        if (originalImage == null) return null;

        float[] motionBlurMatrix = {
                1/9f, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 1/9f, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 1/9f, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 1/9f, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 1/9f, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 1/9f, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 1/9f, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 1/9f, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 1/9f
        };

        return applyConvolutionFilter(new Kernel(9, 9, motionBlurMatrix));
    }

    public BufferedImage emboss() {
        if (originalImage == null) return null;

        float[] embossMatrix = {
                -2, -1, 0,
                -1, 1, 1,
                0, 1, 2
        };

        BufferedImage result = applyConvolutionFilter(new Kernel(3, 3, embossMatrix));
        BufferedImage grayImage = new BufferedImage(
                result.getWidth(), result.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = grayImage.getGraphics();
        g.drawImage(result, 0, 0, null);
        g.dispose();

        return grayImage;
    }

    public BufferedImage medianFilter() {
        if (originalImage == null) return null;

        BufferedImage result = new BufferedImage(
                originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());

        int radius = 1;
        int size = 2 * radius + 1;
        int[] pixels = new int[size * size];

        for (int y = radius; y < originalImage.getHeight() - radius; y++) {
            for (int x = radius; x < originalImage.getWidth() - radius; x++) {
                originalImage.getRGB(x - radius, y - radius, size, size, pixels, 0, size);
                java.util.Arrays.sort(pixels);
                result.setRGB(x, y, pixels[pixels.length / 2]);
            }
        }

        return result;
    }

    public BufferedImage cannyEdgeDetection() {
        if (originalImage == null) return null;

        BufferedImage grayImage = toGrayscale(originalImage);


        float[] gaussianBlurMatrix = {
                1/16f, 2/16f, 1/16f,
                2/16f, 4/16f, 2/16f,
                1/16f, 2/16f, 1/16f
        };
        BufferedImage blurred = applyConvolutionFilter(grayImage, new Kernel(3, 3, gaussianBlurMatrix));
        BufferedImage sobelX = applySobel(blurred, true);
        BufferedImage sobelY = applySobel(blurred, false);


        BufferedImage result = new BufferedImage(
                originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < result.getHeight(); y++) {
            for (int x = 0; x < result.getWidth(); x++) {
                int gx = new Color(sobelX.getRGB(x, y)).getRed();
                int gy = new Color(sobelY.getRGB(x, y)).getRed();
                int gradient = (int) Math.sqrt(gx * gx + gy * gy);
                gradient = Math.min(255, Math.max(0, gradient));
                int edge = gradient > 50 ? 255 : 0;
                result.setRGB(x, y, new Color(edge, edge, edge).getRGB());
            }
        }

        return result;
    }

    public BufferedImage robertsEdgeDetection() {
        if (originalImage == null) return null;

        BufferedImage grayImage = toGrayscale(originalImage);
        BufferedImage result = new BufferedImage(
                grayImage.getWidth(), grayImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < grayImage.getHeight() - 1; y++) {
            for (int x = 0; x < grayImage.getWidth() - 1; x++) {

                int p1 = new Color(grayImage.getRGB(x, y)).getRed();
                int p2 = new Color(grayImage.getRGB(x + 1, y + 1)).getRed();
                int p3 = new Color(grayImage.getRGB(x + 1, y)).getRed();
                int p4 = new Color(grayImage.getRGB(x, y + 1)).getRed();

                int gx = p1 - p2;
                int gy = p3 - p4;

                int gradient = (int) Math.sqrt(gx * gx + gy * gy);
                gradient = Math.min(255, Math.max(0, gradient));
                int edge = gradient > 30 ? 255 : 0; // Simple threshold
                result.setRGB(x, y, new Color(edge, edge, edge).getRGB());
            }
        }

        return result;
    }

    private BufferedImage applyConvolutionFilter(Kernel kernel) {
        return applyConvolutionFilter(originalImage, kernel);
    }

    private BufferedImage applyConvolutionFilter(BufferedImage src, Kernel kernel) {
        BufferedImageOp op = new ConvolveOp(kernel);
        return op.filter(src, null);
    }

    private BufferedImage toGrayscale(BufferedImage src) {
        BufferedImage grayImage = new BufferedImage(
                src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = grayImage.getGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return grayImage;
    }

    private BufferedImage applySobel(BufferedImage src, boolean horizontal) {
        float[] sobelX = {
                -1, 0, 1,
                -2, 0, 2,
                -1, 0, 1
        };

        float[] sobelY = {
                -1, -2, -1,
                0,  0,  0,
                1,  2,  1
        };

        Kernel kernel = horizontal ? new Kernel(3, 3, sobelX) : new Kernel(3, 3, sobelY);
        return applyConvolutionFilter(src, kernel);
    }

    public BufferedImage reset() {
        return originalImage;
    }
}