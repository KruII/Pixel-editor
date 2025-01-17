package com.kruii.tools;

import com.kruii.ui.Tool;
import com.kruii.ui.PixelCanvas;
import com.kruii.model.PixelModel;
import com.kruii.utils.ColorBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class Eraser implements Tool {
    private final JPanel settingsPanel;
    private int eraserSize = 1;

    public Eraser() {
        settingsPanel = new JPanel(new FlowLayout());
        settingsPanel.add(new JLabel("Rozmiar:"));

        JTextField sizeField = new JTextField(String.valueOf(eraserSize), 3);
        sizeField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                try {
                    int val = Integer.parseInt(sizeField.getText().trim());
                    eraserSize = Math.max(1, val); // Minimalny rozmiar = 1
                } catch (NumberFormatException ex) {
                    eraserSize = 1; // Wartość domyślna
                }
                sizeField.setText(String.valueOf(eraserSize));
            }
        });
        settingsPanel.add(sizeField);
    }

    @Override
    public String getName() {
        return "Eraser";
    }

    @Override
    public JPanel getSettingsPanel() {
        return settingsPanel;
    }

    @Override
    public void onMousePress(MouseEvent e, PixelCanvas canvas, PixelModel model) {
        erase(e, canvas, model);
    }

    @Override
    public void onMouseDrag(MouseEvent e, PixelCanvas canvas, PixelModel model) {
        erase(e, canvas, model);
    }

    @Override
    public void onMouseMove(MouseEvent e, PixelCanvas canvas, PixelModel model) {
        // Wyświetlamy border podczas ruchu myszy
        new ColorBorder(e, canvas, Color.RED, eraserSize);
    }

    @Override
    public void onMouseRelease(MouseEvent e, PixelCanvas canvas, PixelModel model) {
        // Nic szczególnego po zwolnieniu
    }

    private void erase(MouseEvent e, PixelCanvas canvas, PixelModel model) {
        int px = canvas.screenToPixelX(e.getX());
        int py = canvas.screenToPixelY(e.getY());
        int half = eraserSize / 2;

        for (int dy = -half; dy <= half; dy++) {
            for (int dx = -half; dx <= half; dx++) {
                int nx = px + dx;
                int ny = py + dy;
                canvas.setPixelSafe(nx, ny, 0); // "0" oznacza przezroczystość
            }
        }
        new ColorBorder(e, canvas, Color.RED, eraserSize);
        canvas.repaint();
    }

}
