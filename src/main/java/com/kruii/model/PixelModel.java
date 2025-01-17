package com.kruii.model;

import java.awt.Color;
import java.util.Arrays;

public class PixelModel {

    private final int width;
    private final int height;
    private final int[][] pixels; 

    public PixelModel(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new int[height][width];
        for (int y = 0; y < height; y++) {
            Arrays.fill(this.pixels[y], 0);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPixel(int x, int y) {
        return pixels[y][x];
    }

    public void setPixel(int x, int y, int val) {
        pixels[y][x] = val;
    }

    public Color getColorForValue(int value) {
        if (value <= 0) {
            return null; // transparent
        }
        int c = Math.min(255, value);
        return new Color(c, c, c); // prosta skala szarości
    }
}
