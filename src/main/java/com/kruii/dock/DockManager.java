package com.kruii.dock;

import javax.swing.*;
import java.awt.*;

public class DockManager {

    private JFrame parentFrame;
    private String highlightedEdge = null; // Przechowuje krawędź do podświetlenia

    public DockManager(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    public void undock(DockablePanel panel) {
        // Usuwamy panel z rodzica (jeśli jest)
        Container parent = panel.getParent();
        if (parent != null) {
            parent.remove(panel);
            parent.revalidate();
            parent.repaint();
        }
        panel.undock();
    }

    public void highlightEdge(String edge) {
        highlightedEdge = edge;
        parentFrame.repaint(); // Wywołaj rysowanie
    }

    public void clearHighlight() {
        highlightedEdge = null;
        parentFrame.repaint(); // Usuń rysowanie
    }

    public void paintHighlights(Graphics g) {
        if (highlightedEdge == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(255, 0, 0, 128)); // Półprzezroczysty czerwony

        Rectangle bounds = parentFrame.getBounds();
        Insets insets = parentFrame.getInsets();

        // Wysokość menu – tak, żeby nie podświetlać powyżej paska menu
        int menuBarHeight = 0;
        JMenuBar mb = parentFrame.getJMenuBar();
        if (mb != null) {
            menuBarHeight = mb.getHeight();
        }

        // Tutaj top jest insets.top + menuBarHeight
        int top = insets.top + menuBarHeight;
        int left = insets.left;
        int right = bounds.width - insets.right;
        int bottom = bounds.height - insets.bottom;

        int thickness = 10; // Grubość podświetlenia

        switch (highlightedEdge) {
            case BorderLayout.WEST:
                g2.fillRect(left, top, thickness, bottom - top);
                break;
            case BorderLayout.EAST:
                g2.fillRect(right - thickness, top, thickness, bottom - top);
                break;
            case BorderLayout.NORTH:
                g2.fillRect(left, top, right - left, thickness);
                break;
            case BorderLayout.SOUTH:
                g2.fillRect(left, bottom - thickness, right - left, thickness);
                break;
        }

        g2.dispose();
    }

    /**
     * Metoda wywoływana w trakcie przeciągania (mouseDragged),
     * aby tymczasowo podświetlić krawędź, jeśli kursor
     * jest w odległości (edgeThreshold) od którejś z krawędzi.
     */
    public void updateHighlight(Point dropPoint, int edgeThreshold) {
        Rectangle bounds = parentFrame.getBounds();
        Insets insets = parentFrame.getInsets();

        // Wysokość menu
        int menuBarHeight = 0;
        if (parentFrame.getJMenuBar() != null) {
            menuBarHeight = parentFrame.getJMenuBar().getHeight();
        }

        int xRelative = dropPoint.x - bounds.x;
        int yRelative = dropPoint.y - bounds.y;

        // realTop = insets.top + menuBarHeight
        int realTop = insets.top + menuBarHeight;
        int realWidth = bounds.width - insets.left - insets.right;
        int realHeight = bounds.height - realTop - insets.bottom;

        // Jeśli kursor jest poza wnętrzem (np. powyżej menubar), to clearHighlight
        if (xRelative < 0 || xRelative > realWidth || yRelative < realTop || yRelative > (realTop + realHeight)) {
            clearHighlight();
            return;
        }

        // Dla dalszych obliczeń – "lokalne" (czyli w obszarze wewnątrz okna)
        int localX = xRelative;
        int localY = yRelative - realTop; // przesunięcie o realTop

        // Odległości do krawędzi
        int distLeft = localX;
        int distRight = (realWidth - localX);
        int distTop = localY;
        int distBottom = (realHeight - localY);

        int minDist = Math.min(Math.min(distLeft, distRight), Math.min(distTop, distBottom));

        if (minDist == distLeft && distLeft < edgeThreshold) {
            highlightEdge(BorderLayout.WEST);
        } else if (minDist == distRight && distRight < edgeThreshold) {
            highlightEdge(BorderLayout.EAST);
        } else if (minDist == distTop && distTop < edgeThreshold) {
            highlightEdge(BorderLayout.NORTH);
        } else if (minDist == distBottom && distBottom < edgeThreshold) {
            highlightEdge(BorderLayout.SOUTH);
        } else {
            clearHighlight();
        }
    }

    /**
     * Próba zadokowania - tylko krawędzie: WEST, EAST, NORTH, SOUTH.
     */
    public boolean tryDock(DockablePanel panel, Point dropPoint) {
        Rectangle bounds = parentFrame.getBounds();
        Insets insets = parentFrame.getInsets();

        int menuBarHeight = 0;
        if (parentFrame.getJMenuBar() != null) {
            menuBarHeight = parentFrame.getJMenuBar().getHeight();
        }

        int xRelative = dropPoint.x - bounds.x;
        int yRelative = dropPoint.y - bounds.y;
        int realTop = insets.top + menuBarHeight;
        int realWidth = bounds.width - insets.left - insets.right;
        int realHeight = bounds.height - realTop - insets.bottom;

        final int edgeThreshold = 100;

        // Sprawdzamy, czy kursor jest wewnątrz obszaru okna (poniżej menubar, powyżej bottom)
        if (xRelative >= 0 && xRelative <= realWidth
            && yRelative >= realTop && yRelative <= (realTop + realHeight)) {

            int localX = xRelative;
            int localY = yRelative - realTop;

            int distLeft = localX;
            int distRight = realWidth - localX;
            int distTop = localY;
            int distBottom = realHeight - localY;

            int minDist = Math.min(Math.min(distLeft, distRight), Math.min(distTop, distBottom));

            if (minDist == distLeft && distLeft < edgeThreshold) {
                panel.dockTo(parentFrame, BorderLayout.WEST);
                return true;
            } else if (minDist == distRight && distRight < edgeThreshold) {
                panel.dockTo(parentFrame, BorderLayout.EAST);
                return true;
            } else if (minDist == distTop && distTop < edgeThreshold) {
                panel.dockTo(parentFrame, BorderLayout.NORTH);
                return true;
            } else if (minDist == distBottom && distBottom < edgeThreshold) {
                panel.dockTo(parentFrame, BorderLayout.SOUTH);
                return true;
            }
        }
        return false;
    }
}
