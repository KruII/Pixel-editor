package com.kruii.tools;

import com.kruii.ui.Tool;
import com.kruii.ui.PixelCanvas;
import com.kruii.model.PixelModel;
import com.kruii.utils.ColorBorder;
import com.kruii.utils.SizeSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class Eraser implements Tool {
    private int toolSize;

    @Override
    public String getName() {
        return "Eraser";
    }

    @Override
    public JPanel getSettingsPanel() {
        JPanel settingsPanel = new JPanel(new GridLayout(2,1));
        settingsPanel.add(SizeSettings.getSettingsPanel());
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
        toolSize = SizeSettings.getToolSize();
        new ColorBorder(e, canvas, Color.RED, toolSize);
    }

    @Override
    public void onMouseRelease(MouseEvent e, PixelCanvas canvas, PixelModel model) {
        // Nic szczególnego po zwolnieniu
    }

    private void erase(MouseEvent e, PixelCanvas canvas, PixelModel model) {
        toolSize = SizeSettings.getToolSize();

        int px = canvas.screenToPixelX(e.getX());
        int py = canvas.screenToPixelY(e.getY());
        int half = toolSize / 2;

        for (int dy = 0; dy < toolSize; dy++) {
            for (int dx = 0; dx < toolSize; dx++) {
                canvas.setPixelSafe(px - half + dx, py -half + dy, 0); // Zakładamy, że "1" to czarny kolor
            }
        }
        new ColorBorder(e, canvas, Color.RED, toolSize);
        canvas.repaint();
    }

}
