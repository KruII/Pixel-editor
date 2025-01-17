package com.kruii.ui;

import javax.swing.*;

import com.kruii.model.PixelModel;

import java.awt.*;
import java.awt.event.*;

public class PixelCanvas extends JPanel {

    private final PixelModel model;

    private double zoomFactor = 8.0; 
    private int offsetX = 0;
    private int offsetY = 0;

    // Tool – ustawia ToolsPanel (lub MainFrame)
    private Tool currentTool;
    private Shape temporaryBorder; // Przechowuje aktualny border
    private Color borderColor = Color.RED; // Kolor borderu

    public void drawTemporaryBorder(int x, int y, int width, int height, Color color) {
        borderColor = color;

        int pixelSize = (int) Math.round(zoomFactor);
        int screenX = offsetX + x * pixelSize;
        int screenY = offsetY + y * pixelSize;

        temporaryBorder = new Rectangle(screenX, screenY, width * pixelSize, height * pixelSize);
        repaint();
    }

    public void clearTemporaryGraphics() {
        temporaryBorder = null; // Usuń border
        repaint();
    }

    public PixelCanvas(PixelModel model) {
        this.model = model;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (currentTool != null) {
                    currentTool.onMousePress(e, PixelCanvas.this, model);
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentTool != null) {
                    currentTool.onMouseRelease(e, PixelCanvas.this, model);
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentTool != null) {
                    currentTool.onMouseDrag(e, PixelCanvas.this, model);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (currentTool != null) {
                    currentTool.onMouseMove(e, PixelCanvas.this, model);
                }
            }
        });

        // Na starcie centrowanie
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                centerCanvas();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Tło panelu
        g.setColor(new Color(0x303030));
        g.fillRect(0, 0, getWidth(), getHeight());

        Graphics2D g2 = (Graphics2D) g.create();

        int pixelSize = (int) Math.round(zoomFactor);

        // Najpierw rysujemy miarkę
        drawRuler(g2, pixelSize);

        // Rysujemy piksele
        int w = model.getWidth();
        int h = model.getHeight();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int screenX = offsetX + x * pixelSize;
                int screenY = offsetY + y * pixelSize;

                // Transparent => szachownica
                Color col = model.getColorForValue(model.getPixel(x, y));
                if (col == null) {
                    // kafelek
                    if (((x + y) & 1) == 0) {
                        col = new Color(0x333333);
                    } else {
                        col = new Color(0x444444);
                    }
                }
                g2.setColor(col);
                g2.fillRect(screenX, screenY, pixelSize, pixelSize);
            }
        }
        if (temporaryBorder != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(2)); // Grubość linii
            g2.draw(temporaryBorder);
        }

        g2.dispose();
    }
    

    private void drawRuler(Graphics2D g2, int pixelSize) {
        g2.setColor(Color.LIGHT_GRAY);
    
        // Rysujemy górną miarkę
        for (int x = 0; x <= model.getWidth(); x++) {
            int screenX = offsetX + x * pixelSize;
    
            if (x % 5 == 0) {
                // Co 5 pikseli większa kreska + liczba
                g2.drawLine(screenX, offsetY, screenX, offsetY - 10);
                g2.drawString(String.valueOf(x), screenX + 2, offsetY - 12);
            } else {
                // Pozostałe mniejsze kreski
                g2.drawLine(screenX, offsetY, screenX, offsetY - 5);
            }
        }
    
        // Rysujemy lewą miarkę
        for (int y = 0; y <= model.getHeight(); y++) {
            int screenY = offsetY + y * pixelSize;
    
            if (y % 5 == 0) {
                // Co 5 pikseli większa kreska + liczba
                g2.drawLine(offsetX, screenY, offsetX - 10, screenY);
                g2.drawString(String.valueOf(y), offsetX - 30, screenY + 5);
            } else {
                // Pozostałe mniejsze kreski
                g2.drawLine(offsetX, screenY, offsetX - 5, screenY);
            }
        }
    }
    

    public void centerCanvas() {
        int pixelSize = (int) Math.round(zoomFactor);
        int contentWidth = model.getWidth() * pixelSize;
        int contentHeight = model.getHeight() * pixelSize;

        int panelW = getWidth();
        int panelH = getHeight();

        offsetX = (panelW - contentWidth) / 2;
        offsetY = (panelH - contentHeight) / 2;
        repaint();
    }

    public void setTool(Tool tool) {
        this.currentTool = tool;
    }
    public Tool getTool() {
        return currentTool;
    }

    public void setZoomFactor(double z) {
        zoomFactor = Math.max(1.0, z);
        centerCanvas();
    }
    public double getZoomFactor() {
        return zoomFactor;
    }

    // Konwersje ekranu na piksel
    public int screenToPixelX(int screenX) {
        int pixelSize = (int)Math.round(zoomFactor);
        return (screenX - offsetX) / pixelSize;
    }
    public int screenToPixelY(int screenY) {
        int pixelSize = (int)Math.round(zoomFactor);
        return (screenY - offsetY) / pixelSize;
    }

    // Bezpieczne ustawianie piksela
    public void setPixelSafe(int x, int y, int val) {
        if (x < 0 || y < 0 || x >= model.getWidth() || y >= model.getHeight()) {
            return;
        }
        model.setPixel(x, y, val);
    }
}
