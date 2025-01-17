package com.kruii.tools;

import com.kruii.ui.Tool;
import com.kruii.ui.PixelCanvas;
import com.kruii.model.PixelModel;
import com.kruii.utils.ColorBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class Pencil implements Tool {

    private final JPanel settingsPanel; 
    private int pencilSize = 1;
    private int pencilValue = 200; // od 0..255 (im więcej, tym jaśniejszy)

    public Pencil() {
        settingsPanel = new JPanel(new FlowLayout());
        settingsPanel.add(new JLabel("Rozmiar:"));
        JTextField sizeField = new JTextField(String.valueOf(pencilSize), 3);
        sizeField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                try {
                    int val = Integer.parseInt(sizeField.getText());
                    pencilSize = Math.max(1, val);
                } catch (NumberFormatException ex) { 
                    pencilSize=1; 
                }
                sizeField.setText(String.valueOf(pencilSize));
            }
        });
        settingsPanel.add(sizeField);

        settingsPanel.add(new JLabel("Kolor(0-255):"));
        JTextField valField = new JTextField(String.valueOf(pencilValue), 3);
        valField.addActionListener(e -> {
            try {
                int val = Integer.parseInt(valField.getText());
                pencilValue = Math.min(255, Math.max(0, val));
            } catch (NumberFormatException ex) { /* ignoruj */ }
        });
        settingsPanel.add(valField);
    }

    @Override
    public String getName() {
        return "Pencil";
    }

    @Override
    public JPanel getSettingsPanel() {
        return settingsPanel;
    }

    @Override
    public void onMousePress(MouseEvent e, PixelCanvas canvas, PixelModel model) {
        draw(e, canvas, model);
    }

    @Override
    public void onMouseDrag(MouseEvent e, PixelCanvas canvas, PixelModel model) {
        draw(e, canvas, model);
    }

    @Override
    public void onMouseMove(MouseEvent e, PixelCanvas canvas, PixelModel model) {
        new ColorBorder(e, canvas, Color.BLUE, pencilSize);
    }

    @Override
    public void onMouseRelease(MouseEvent e, PixelCanvas canvas, PixelModel model) {
        // nic specjalnego
    }

    private void draw(MouseEvent e, PixelCanvas canvas, PixelModel model) {
        int px = canvas.screenToPixelX(e.getX());
        int py = canvas.screenToPixelY(e.getY());
        int half = pencilSize / 2;
        for (int dy = -half; dy < pencilSize - half; dy++) {
            for (int dx = -half; dx < pencilSize - half; dx++) {
                int nx = px + dx;
                int ny = py + dy;
                canvas.setPixelSafe(nx, ny, pencilValue);
            }
        }
        new ColorBorder(e, canvas, Color.BLUE, pencilSize);
        canvas.repaint();
    }
}
