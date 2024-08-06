package com.github.javarar.animal.faces;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class AnimalFacesPipeline {
    // загрузка файлов из директории (можно параллельно)
    public static File[] readImages(String path) {
        return new File(path).listFiles();
    }

    // изменение картинки (можно параллельно, разбиение зависит от метода трансформации)
    public static BufferedImage changeImage(File file) {
        byte[] imageBytes;
        try {
            imageBytes = Files.readAllBytes(file.toPath());
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            return SobelFilter.applyFilter(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // запись измененных картинок на диск
    public static void saveImage(File path, BufferedImage image) {
        try {
            ImageIO.write(image, "jpg", path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
