package com.kruii.tools;

import com.kruii.ui.Tool;
import com.kruii.utils.Circle;
import com.kruii.utils.ColorBorder;
import com.kruii.utils.ColorSettings;
import com.kruii.utils.ColorUtils;
import com.kruii.utils.SizeSettings;
import com.kruii.ui.PixelCanvas;
import com.kruii.model.PixelModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Brush implements Tool {

    private int toolSize;
    private int toolColor;

    @Override
    public String getName() {
        return "Brush";
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
        new ColorBorder(e, canvas, Color.BLUE, toolSize, 1);
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

        float hardness = 0.8f;
        paintWithBrush(px, py, toolSize, toolSize, hardness, toolColor, canvas, model);
                
        new ColorBorder(e, canvas, Color.BLUE, toolSize, 1);
        canvas.repaint();
    }

    public void paintWithBrush(int cx, int cy, int sizeX, int sizeY, float hardness, int colorIndex, PixelCanvas canvas, PixelModel model) {
        // 1. Tworzymy tablicę 2D, gdzie circleBorder[y][x] == 1 oznacza piksel do malowania.
        int[][] circleBorder = Circle.getPixelCircleBorder(
            sizeX,
            sizeY,
            (int) Math.round(canvas.getZoomFactor())
        );
    
        int height = circleBorder.length;
        int width  = (height > 0) ? circleBorder[0].length : 0;
    
        // Ustalamy dla pędzla parzystego centralny obszar 2x2
        boolean evenBrush = (sizeX % 2 == 0) && (sizeY % 2 == 0);
        int centerStartX = 0, centerEndX = 0, centerStartY = 0, centerEndY = 0;
        if (evenBrush) {
            centerStartX = sizeX / 2 - 1;
            centerEndX   = sizeX / 2;     // centralne pozycje: centerStartX oraz centerEndX
            centerStartY = sizeY / 2 - 1;
            centerEndY   = sizeY / 2;
        }
    
        // 2. Dla każdego wiersza zbieramy listy kolumn, w których jest 1
        @SuppressWarnings("unchecked")
        List<Integer>[] rowPositions = new List[height];
        for (int y = 0; y < height; y++) {
            rowPositions[y] = new ArrayList<>();
            for (int x = 0; x < width; x++) {
                if (circleBorder[y][x] == 1) {
                    rowPositions[y].add(x);
                }
            }
        }
    
        // 3. Dla każdej kolumny zbieramy listy wierszy, w których jest 1
        @SuppressWarnings("unchecked")
        List<Integer>[] colPositions = new List[width];
        for (int x = 0; x < width; x++) {
            colPositions[x] = new ArrayList<>();
            for (int y = 0; y < height; y++) {
                if (circleBorder[y][x] == 1) {
                    colPositions[x].add(y);
                }
            }
        }
    
        // 4. Iterujemy po wszystkich pikselach, gdzie circleBorder == 1
        for (int cirY = 0; cirY < height; cirY++) {
            for (int cirX = 0; cirX < width; cirX++) {
                if (circleBorder[cirY][cirX] == 1) {
    
                    // Wyznaczamy rzeczywiste współrzędne na obrazie
                    int nx = cx + cirX - sizeX / 2;
                    int ny = cy + cirY - sizeY / 2;
    
                    // Sprawdzamy, czy mieszczymy się w granicach
                    if (nx < 0 || ny < 0 || nx >= model.getWidth() || ny >= model.getHeight()) {
                        continue;
                    }
    
                    // Obliczenie alfa – standardowo przez odległość od środka w poziomie i pionie
                    float rowAlpha = 1.0f;
                    List<Integer> rList = rowPositions[cirY];
                    if (rList.size() > 1) {
                        int minX = rList.get(0);
                        int maxX = rList.get(rList.size() - 1);
                        float centerX = (minX + maxX) / 2.0f;
                        float halfSpanX = (maxX - minX) / 2.0f;
                        float distX = Math.abs(cirX - centerX);
                        if (halfSpanX > 0) {
                            rowAlpha = 1.0f - (distX / halfSpanX);
                            rowAlpha = Math.max(0f, Math.min(1f, rowAlpha));
                        }
                    }
    
                    float colAlpha = 1.0f;
                    List<Integer> cList = colPositions[cirX];
                    if (cList.size() > 1) {
                        int minY = cList.get(0);
                        int maxY = cList.get(cList.size() - 1);
                        float centerY = (minY + maxY) / 2.0f;
                        float halfSpanY = (maxY - minY) / 2.0f;
                        float distY = Math.abs(cirY - centerY);
                        if (halfSpanY > 0) {
                            colAlpha = 1.0f - (distY / halfSpanY);
                            colAlpha = Math.max(0f, Math.min(1f, colAlpha));
                        }
                    }
    
                    // Domyślnie finalna alfa
                    float alpha = rowAlpha * colAlpha * hardness;
    
                    // Jeśli pędzel jest parzysty, ustawiamy centralny kwadrat 2x2 na pełną twardość (bez gradientu)
                    if (evenBrush && (cirX == centerStartX || cirX == centerEndX) && (cirY == centerStartY || cirY == centerEndY)) {
                        alpha = hardness;
                    } else {
                        alpha = Math.max(alpha, 0.05f);
                    }
    
                    // Mieszanie kolorów
                    Color fg = model.getPalette()[colorIndex];               // kolor "pędzla"
                    Color bg = model.getPalette()[model.getPixel(nx, ny)];       // kolor tła
                    Color blended = ColorUtils.blendColors(fg, bg, alpha);
    
                    // Najbliższy kolor z palety
                    int newIndex = ColorUtils.findClosestColorInPalette(blended, model.getPalette());
                    model.setPixel(nx, ny, newIndex);
                }
            }
        }
    }
    
}    

