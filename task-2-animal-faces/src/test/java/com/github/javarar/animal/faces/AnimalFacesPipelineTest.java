package com.github.javarar.animal.faces;

import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

import static com.github.javarar.animal.faces.AnimalFacesPipeline.*;
import static org.junit.jupiter.api.Assertions.*;

class AnimalFacesPipelineTest {

    @Test
    @SneakyThrows
    void pipeline() {
        val readPath = "src/main/resources/dataset";
        val savePath = "src/main/resources/processed";

        record ImageWithName(String name, BufferedImage image) {}
        Arrays.stream(readImages(readPath)).limit(100)
                .map(file -> new ImageWithName(file.getName(), changeImage(file)))
                .forEach(image -> saveImage(new File("%s/%s".formatted(savePath, image.name())), image.image()));
    }
}