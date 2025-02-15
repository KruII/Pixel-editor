package com.kruii.utils;

import java.awt.Color;
import java.awt.event.MouseEvent;

import com.kruii.ui.PixelCanvas;

public class ColorBorder {

    public ColorBorder(MouseEvent e, PixelCanvas canvas, Color color, int size) {
        this(e, canvas, color, size, 0); // Wywołujemy pełny konstruktor z type = 0
    }

    public ColorBorder(MouseEvent e, PixelCanvas canvas, Color color, int size, int type) {
        int px = canvas.screenToPixelX(e.getX());
        int py = canvas.screenToPixelY(e.getY());
        int half = size / 2;

        // Rysujemy border wokół obszaru
        canvas.drawTemporaryBorder(px - half, py - half, size, size, color, type);
    }
}
