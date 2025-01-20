package com.kruii.tools;

import com.kruii.ui.PixelCanvas;
import com.kruii.ui.Tool;
import com.kruii.model.PixelModel;
import com.kruii.utils.Circle;
import com.kruii.utils.ColorBorder;
import com.kruii.utils.ColorSettings;
import com.kruii.utils.ColorUtils;
import com.kruii.utils.SizeSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Brush implements Tool {

    private int toolSize;
    private Color toolColor;  // Będziemy pobierać z ColorSettings

    @Override
    public String getName() {
        return "Brush";
    }

    @Override
    public JPanel getSettingsPanel() {
        // Panel z ustawieniami: rozmiar i kolor
        JPanel settingsPanel = new JPanel(new GridLayout(2, 1));
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
        // Wyświetlamy obrys pędzla
        new ColorBorder(e, canvas, Color.GREEN, toolSize, 1);
    }

    @Override
    public void onMouseRelease(MouseEvent e, PixelCanvas canvas, PixelModel model) {
        // Nic specjalnego
    }

    private void draw(MouseEvent e, PixelCanvas canvas, PixelModel model) {
        toolSize = SizeSettings.getToolSize();
        toolColor = ColorSettings.getSelectedColor(); // kolor wybrany w JColorChooser

        int px = canvas.screenToPixelX(e.getX());
        int py = canvas.screenToPixelY(e.getY());

        // Przykładowa "twardość" pędzla
        float hardness = 0.8f;

        // Rysowanie okrągłym pędzlem
        paintWithBrush(px, py, toolSize, toolSize, hardness, toolColor, canvas, model);

        // Obrys pędzla
        new ColorBorder(e, canvas, Color.GREEN, toolSize, 1);
        canvas.repaint();
    }

    /**
     * Metoda odpowiedzialna za malowanie okrągłym pędzlem z uwzględnieniem twardości.
     */
    public void paintWithBrush(int cx, int cy, int sizeX, int sizeY,
                               float hardness, Color brushColor,
                               PixelCanvas canvas, PixelModel model) 
    {
        // 1. Pobieramy maskę pikseli okręgu (1 = wewnątrz obrysu, 0 = poza)
        int[][] circleMask = Circle.getPixelCircleBorder(
            sizeX,
            sizeY,
            (int)Math.round(canvas.getZoomFactor())
        );

        int maskHeight = circleMask.length;
        int maskWidth  = (maskHeight > 0) ? circleMask[0].length : 0;

        // Sprawdzamy, czy rozmiar pędzla jest parzysty (wówczas "środek" to 2x2)
        boolean evenBrush = (sizeX % 2 == 0) && (sizeY % 2 == 0);
        int centerStartX = 0, centerEndX = 0, centerStartY = 0, centerEndY = 0;
        if (evenBrush) {
            centerStartX = sizeX / 2 - 1;
            centerEndX   = sizeX / 2;
            centerStartY = sizeY / 2 - 1;
            centerEndY   = sizeY / 2;
        }

        // 2. Dla każdego wiersza zbieramy listy x, gdzie circleMask[y][x] = 1
        @SuppressWarnings("unchecked")
        List<Integer>[] rowPositions = new List[maskHeight];
        for (int y = 0; y < maskHeight; y++) {
            rowPositions[y] = new ArrayList<>();
            for (int x = 0; x < maskWidth; x++) {
                if (circleMask[y][x] == 1) {
                    rowPositions[y].add(x);
                }
            }
        }

        // 3. Dla każdej kolumny zbieramy listy y, gdzie circleMask[y][x] = 1
        @SuppressWarnings("unchecked")
        List<Integer>[] colPositions = new List[maskWidth];
        for (int x = 0; x < maskWidth; x++) {
            colPositions[x] = new ArrayList<>();
            for (int y = 0; y < maskHeight; y++) {
                if (circleMask[y][x] == 1) {
                    colPositions[x].add(y);
                }
            }
        }

        // 4. Iteracja po pikselach wewnątrz okręgu
        for (int cirY = 0; cirY < maskHeight; cirY++) {
            for (int cirX = 0; cirX < maskWidth; cirX++) {
                if (circleMask[cirY][cirX] == 1) {

                    // Przeliczenie na współrzędne w PixelModel
                    int nx = cx + cirX - sizeX / 2;
                    int ny = cy + cirY - sizeY / 2;

                    // Sprawdzenie granic
                    if (nx < 0 || ny < 0 || nx >= model.getWidth() || ny >= model.getHeight()) {
                        continue;
                    }

                    // Obliczamy przezroczystość w poziomie (rowAlpha)
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

                    // Obliczamy przezroczystość w pionie (colAlpha)
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

                    // Ostateczna alpha = rowAlpha * colAlpha * hardness
                    float alpha = rowAlpha * colAlpha * hardness;

                    // Jeżeli pędzel jest parzysty, dla środkowego kwadratu 2x2 nadajemy pełną twardość
                    if (evenBrush
                        && (cirX == centerStartX || cirX == centerEndX)
                        && (cirY == centerStartY || cirY == centerEndY)) 
                    {
                        alpha = hardness;
                    } else {
                        // Minim. poziom wypełnienia
                        alpha = Math.max(alpha, 0.05f);
                    }

                    // --- MIESZANIE KOLORÓW (alpha-blend) ---
                    
                    // Kolor pędzla (FG)
                    Color fg = brushColor;

                    // Aktualny kolor w modelu (BG)
                    int currentARGB = model.getPixel(nx, ny);
                    Color bg = (currentARGB == 0)
                            ? new Color(0, 0, 0, 0) // przezroczysty
                            : new Color(currentARGB, true);

                    // Mieszamy za pomocą naszej metody blendColors(fg, bg, alpha)
                    Color blended = ColorUtils.blendColors(fg, bg, alpha);

                    // Zapisujemy wynikowy kolor w modelu, jako ARGB
                    model.setPixel(nx, ny, blended.getRGB());
                }
            }
        }
    }
}
