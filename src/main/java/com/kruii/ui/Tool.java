package com.kruii.ui;

import java.awt.event.MouseEvent;
import javax.swing.*;

import com.kruii.model.PixelModel;

public interface Tool {
    String getName();
    JPanel getSettingsPanel();

    // Wywoływane z PixelCanvas w zdarzeniach myszy
    void onMousePress(MouseEvent e, PixelCanvas canvas, PixelModel model);
    void onMouseDrag(MouseEvent e, PixelCanvas canvas, PixelModel model);
    void onMouseRelease(MouseEvent e, PixelCanvas canvas, PixelModel model);
    void onMouseMove(MouseEvent e, PixelCanvas pixelCanvas, PixelModel model);
}
