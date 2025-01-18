package com.kruii.tools;

import com.kruii.ui.Tool;
import com.kruii.ui.PixelCanvas;
import com.kruii.model.PixelModel;
import com.kruii.utils.ColorBorder;
import com.kruii.utils.ColorSettings;
import com.kruii.utils.SizeSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class Pencil implements Tool {

    private int toolSize;
    private int toolColor;

    @Override
    public String getName() {
        return "Pencil";
    }

    @Override
    public JPanel getSettingsPanel() {
        JPanel settingsPanel = new JPanel(new GridLayout(2,1));
        settingsPanel.add(SizeSettings.getSettingsPanel());
        settingsPanel.add(ColorSettings.getSettingsPanel());
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
        toolSize = SizeSettings.getToolSize();
        new ColorBorder(e, canvas, Color.BLUE, toolSize);
    }

    @Override
    public void onMouseRelease(MouseEvent e, PixelCanvas canvas, PixelModel model) {
        // nic specjalnego
    }

    private void draw(MouseEvent e, PixelCanvas canvas, PixelModel model) {
        toolSize = SizeSettings.getToolSize();
        toolColor = ColorSettings.getToolValue();

        int px = canvas.screenToPixelX(e.getX());
        int py = canvas.screenToPixelY(e.getY());
        int half = toolSize / 2;
        
        for (int dy = 0; dy < toolSize; dy++) {
            for (int dx = 0; dx < toolSize; dx++) {
                canvas.setPixelSafe(px - half + dx, py -half + dy, toolColor); // Zakładamy, że "1" to czarny kolor
            }
        }
        new ColorBorder(e, canvas, Color.BLUE, toolSize);
        canvas.repaint();
    }
}
