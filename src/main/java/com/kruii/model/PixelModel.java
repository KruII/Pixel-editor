package com.kruii.model;

import java.awt.Color;
import java.util.Arrays;

public class PixelModel {

    private final int width;
    private final int height;
    private final int[][] pixels; // Teraz przechowujemy pełen ARGB

    public PixelModel(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new int[height][width];
        
        // Domyślnie ustawiamy 0 (co potraktujemy jako "przezroczysty" / brak koloru)
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

    /**
     * Zwraca surowy int (ARGB) z tablicy.
     * Jeśli wartość to 0, potraktujemy ją jako brak koloru (transparent).
     */
    public int getPixel(int x, int y) {
        return pixels[y][x];
    }
    
    public void setPixel(int x, int y, int argb) {
        pixels[y][x] = argb;
    }

    /**
     * Zwraca obiekt Color na podstawie surowego ARGB.
     * Jeśli value == 0, zwracamy null, by potraktować to jako przezroczystość.
     */
    public Color getColorForValue(int value) {
        if (value == 0) {
            return null; // brak koloru / przezroczysty
        }
        return new Color(value, true);  // tworzy Color z ARGB
    }
}
