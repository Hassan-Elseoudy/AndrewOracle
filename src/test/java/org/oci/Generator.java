package org.oci;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Generator {

    public static File createFileWithEncoding(String encoding) throws IOException {
        File newFile = new File("file_with_" + encoding + "_encoding");
        try (FileWriter writer = new FileWriter(newFile)) {
            writer.write("This is a test file with " + encoding + " encoding.");
        }
        return newFile;
    }

    public static File createFileWithContentType(String contentType, String fileName) throws IOException {
        File newFile = new File(fileName);
        try (FileWriter writer = new FileWriter(newFile)) {
            if (contentType.equals("application/json")) {
                writer.write("{\"key\":\"value\"}");
            } else if (contentType.equals("image/png")) {
                return createSamplePNGImage(fileName);
            }
        }
        return newFile;
    }

    private static File createSamplePNGImage(String fileName) throws IOException {
        File file = new File(fileName);
        int width = 100;
        int height = 100;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // fill the image with some random pixels
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bufferedImage.setRGB(i, j, (int) (Math.random() * 0xFFFFFF));
            }
        }
        ImageIO.write(bufferedImage, "png", file);
        return file;
    }


}
