package com.github.javarar.animal.faces;

import lombok.val;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.function.BiFunction;

public class SobelFilter {
    private static final int[][] G_x = new int[][]{{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
    private static final int[][] G_y = new int[][]{{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static BufferedImage applyFilter(BufferedImage input) {
        val result = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());

        val futures = new ArrayList<Future<Result>>();
        for (int i = 1; i < result.getWidth() - 1; i++) {
            for (int j = 1; j < result.getHeight() - 1; j++) {
                futures.add(executor.submit(new ProcessPixel(j, i, input)));
            }
        }

        futures.stream()
                .map(task -> {
                    try {
                        return task.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(pixel -> result.setRGB(pixel.x(), pixel.y(), pixel.rgb()));

        return result;
    }

    private static class ProcessPixel implements Callable<Result> {
        private final int x;
        private final int y;
        private final BufferedImage image;
        private final BiFunction<Integer, Integer, Integer> getRed;
        private final BiFunction<Integer, Integer, Integer> getGreen;
        private final BiFunction<Integer, Integer, Integer> getBlue;

        public ProcessPixel(int x, int y, BufferedImage image) {
            this.x = x;
            this.y = y;
            this.image = image;
            this.getRed = (X,Y) -> (image.getRGB(X,Y) >> 16) & 0xFF;
            this.getGreen = (X,Y) -> (image.getRGB(X,Y) >> 8) & 0xFF;
            this.getBlue = (X,Y) -> image.getRGB(X,Y) & 0xFF;
        }

        @Override
        public Result call() {
            val red = processSubPixel(getPixelArea(getRed, x,y));
            val green = processSubPixel(getPixelArea(getGreen, x,y));
            val blue = processSubPixel(getPixelArea(getBlue, x,y));
            val alpha = image.getColorModel().getAlpha(image.getRGB(x,y));

            val rgb = (alpha << 24) | (red << 16) | (green << 8) | blue;
            return new Result(rgb, x, y);
        }

        private int[][] getPixelArea(BiFunction<Integer, Integer, Integer> getPixel, int x, int y) {
            return new int[][]{
                    {getPixel.apply(x - 1, y - 1), getPixel.apply(x, y - 1), getPixel.apply(x + 1, y - 1)},
                    {getPixel.apply(x - 1, y), getPixel.apply(x, y), getPixel.apply(x + 1, y)},
                    {getPixel.apply(x - 1, y + 1), getPixel.apply(x, y + 1), getPixel.apply(x + 1, y + 1)}
            };
        }

        private int processSubPixel(int[][] pixelArea) {
            int gx = 0;
            int gy = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    gx += pixelArea[i][j] * G_x[i][j];
                    gy += pixelArea[i][j] * G_y[i][j];
                }
            }
            return (int) Math.sqrt(Math.pow(gx, 2) + Math.pow(gy, 2));
        }
    }

    private record Result(int rgb, int x, int y) {
    }
}
