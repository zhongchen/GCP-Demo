package com.google.pso.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

public class ImageProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageProcessor.class);

    private ImageProcessor() {}

    public static BufferedImage resize(BufferedImage image, int factor) {
        int width = image.getWidth() / factor;
        int height = image.getHeight() / factor;
        Image sImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        final BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = result.createGraphics();
        try {
            g2d.drawImage(sImage, 0, 0, null);
        }
        finally {
            g2d.dispose();
        }
        return image;
    }

    public static Optional<byte[]> imageToBytes(BufferedImage image) {
        try (ByteArrayOutputStream ostream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "JPG", ostream);
            return Optional.of(ostream.toByteArray());
        }
        catch (IOException e) {
            LOGGER.warn("Unable to convert image to byte array", e);
        }
        return Optional.empty();
    }

    public static BufferedImage pageToImage(PDDocument pdf, int i) throws IOException {
        PDFRenderer pdfRenderer = new PDFRenderer(pdf);
        BufferedImage fullimage = pdfRenderer.renderImageWithDPI(i, 200, ImageType.RGB);

        Image sImage = fullimage.getWidth() < fullimage.getHeight()
                ? fullimage.getScaledInstance(2500, 3500, Image.SCALE_SMOOTH)
                : fullimage.getScaledInstance(3500, 2500, Image.SCALE_SMOOTH);
        final BufferedImage image = new BufferedImage(sImage.getWidth(null), sImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        try {
            g.drawImage(sImage, 0, 0, null);
        }
        finally {
            g.dispose();
        }
        return image;
    }
}
