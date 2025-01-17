package com.kruii.dock;

import javax.swing.*;

import com.kruii.ui.PixelCanvas;
import com.kruii.ui.ToolsPanel;

import java.awt.*;
import java.awt.event.*;

public class DockablePanel extends JPanel {

    private boolean isFloating = false;  // Czy panel jest odłączony
    private JDialog floatingDialog;      // Okno (undecorated) przy odpięciu
    private JFrame parentFrame;          // Okno główne
    private DockManager dockManager;     // Zarządca dokowania
    private JCheckBoxMenuItem showToolsMenuItem; // Referencja do pozycji w menu (by odznaczyć, gdy user zamknie)

    private ToolsPanel toolsPanel;

    private JPanel dragHandle;           // Pasek do przeciągania
    private Point dragStart;             // Pozycja startowa panelu podczas mouseDragged w trakcie przeciągania
    private static final int EDGE_THRESHOLD = 50; // Odległość od krawędzi do dokowania

    // Menu kontekstowe
    private JPopupMenu contextMenu;
    private JMenuItem closeItem;
    private JCheckBoxMenuItem alwaysOnTopItem;

    private int resizeX, resizeY;

    public DockablePanel(JFrame parentFrame, DockManager dockManager, JCheckBoxMenuItem showToolsMenuItem, PixelCanvas canvas) {
        super(new BorderLayout());
        this.parentFrame = parentFrame;
        this.dockManager = dockManager;
        this.showToolsMenuItem = showToolsMenuItem;

        setBackground(new Color(0x995555));

        resizeX = 200;
        resizeY = 50;

        // Uchwyty do przeciągania
        dragHandle = new JPanel(null);
        dragHandle.setBackground(new Color(0x444444));
        dragHandle.setPreferredSize(new Dimension(resizeX, 14));
        dragHandle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(dragHandle, BorderLayout.NORTH);

        // Prosta zawartość demo
        toolsPanel = new ToolsPanel(canvas, parentFrame);
        toolsPanel.setForeground(Color.WHITE);
        toolsPanel.setPreferredSize(new Dimension(resizeX, resizeY));

        add(toolsPanel, BorderLayout.CENTER);

        // Obsługa przeciągania
        DragListener dragListener = new DragListener();
        dragHandle.addMouseListener(dragListener);
        dragHandle.addMouseMotionListener(dragListener);

        // Menu kontekstowe (prawy przycisk)
        contextMenu = new JPopupMenu();
        closeItem = new JMenuItem("Close");
        closeItem.addActionListener(e -> closePanel());
        contextMenu.add(closeItem);

        alwaysOnTopItem = new JCheckBoxMenuItem("Always on top");
        alwaysOnTopItem.addActionListener(e -> setFloatingAlwaysOnTop(alwaysOnTopItem.isSelected()));
        contextMenu.add(alwaysOnTopItem);

        dragHandle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    contextMenu.show(dragHandle, e.getX(), e.getY());
                }
            }
        });

        ResizingPanel resizingPanel = new ResizingPanel();
        addMouseListener(resizingPanel);
        addMouseMotionListener(resizingPanel);
    }

    private class DragListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                dragStart = e.getPoint();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e) && dragStart != null) {
                if (!isFloating) {
                    // Odpinamy panel (pierwsze ruszenie)
                    dockManager.undock(DockablePanel.this);
                    isFloating = true;
                }
                if (floatingDialog != null) {
                    dragHandle.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    Point mouseOnScreen = e.getLocationOnScreen();
                    floatingDialog.setLocation(mouseOnScreen.x - 1, mouseOnScreen.y - 1);

                    // Wywołaj tymczasowe podświetlenie krawędzi w trakcie przeciągania
                    dockManager.updateHighlight(mouseOnScreen, EDGE_THRESHOLD);
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e) && isFloating && floatingDialog != null) {
                // Przestań podświetlać
                dockManager.clearHighlight();

                // Próba dokowania w momencie puszczenia
                Point dropPoint = e.getLocationOnScreen();
                boolean docked = dockManager.tryDock(DockablePanel.this, dropPoint);
                if (docked) {
                    // Zadokowano
                    isFloating = false;
                    floatingDialog.dispose();
                    floatingDialog = null;
                }
            }
        }
    }

    /**
     * Dokowanie do określonej krawędzi (bez środka).
     */
    public void dockTo(JFrame parent, String position) {
        parent.getContentPane().add(this, position);
        parent.revalidate();
        parent.repaint();
        dragHandle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Odpinanie (tworzymy niestandardowe okno bez dekoracji, z obsługą resize).
     */
    public void undock() {
        floatingDialog = new JDialog(parentFrame);
        floatingDialog.setUndecorated(true); 
        // Zostawiamy setResizable(true), ale w oknach undecorated
        // trzeba samodzielnie zaimplementować przesuwanie krawędzi.
        floatingDialog.setResizable(true);
        floatingDialog.setSize(resizeX, resizeY);
        floatingDialog.setLocationRelativeTo(null);
        floatingDialog.add(this);

        floatingDialog.setVisible(true);
        // Domyślnie alwaysOnTop = false
        floatingDialog.setAlwaysOnTop(false);
        alwaysOnTopItem.setSelected(false);
    }

    public void closePanel() {
        // Zamknięcie przez menu kontekstowe
        if (isFloating) {
            // Zamknij okno floatingDialog
            if (floatingDialog != null) {
                floatingDialog.dispose();
                floatingDialog = null;
                isFloating = false;
            }
        } else {
            // Zadokowany w głównym oknie - usuwamy z rodzica
            Container parent = this.getParent();
            if (parent != null) {
                parent.remove(this);
                parent.revalidate();
                parent.repaint();
            }
        }
        // Odznacz w menu
        if (showToolsMenuItem != null) {
            showToolsMenuItem.setSelected(false);
        }
    }

    private void setFloatingAlwaysOnTop(boolean onTop) {
        if (isFloating && floatingDialog != null) {
            floatingDialog.setAlwaysOnTop(onTop);
        } else {
            // Jeśli nie jest w trybie floating, upewnij się, że opcja jest wyłączona
            alwaysOnTopItem.setSelected(false); // Odznacz checkbox, jeśli jest zaznaczony
        }
    }

    // Klasa obsługująca resize
    private class ResizingPanel extends MouseAdapter {
    
        private static final int RESIZE_MARGIN = 5;
        private boolean resizing = false;
        private int cursorType = Cursor.DEFAULT_CURSOR;
        private Point prevMouse;
    
        @Override
        public void mouseMoved(MouseEvent e) {
            updateCursor(e.getPoint());
        }
    
        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e) && cursorType != Cursor.DEFAULT_CURSOR) {
                resizing = true;
                prevMouse = e.getPoint();
            }
        }
    
        @Override
        public void mouseDragged(MouseEvent e) {
            if (resizing && prevMouse != null) {
                Rectangle bounds;
                if (isFloating) {
                    bounds = floatingDialog.getBounds();
                }else{
                    bounds = getBounds();
                }
                int dx = e.getX() - prevMouse.x;
                int dy = e.getY() - prevMouse.y;
    
                switch (cursorType) {
                    case Cursor.E_RESIZE_CURSOR:
                        resizeX += dx;
                        break;
                    case Cursor.W_RESIZE_CURSOR:
                        resizeX -= dx;
                        break;
                    case Cursor.S_RESIZE_CURSOR:
                        resizeY += dy;
                        break;
                    case Cursor.SE_RESIZE_CURSOR:
                        resizeX += dx;
                        resizeY += dy;
                        break;
                    default:
                        break;
                }
    
                // Zabezpieczenie przed zbyt małym rozmiarem
                if (resizeX < 200) resizeX = 200;
                if (resizeY < 50) resizeY = 50;
                
                if (isFloating) {
                    floatingDialog.setBounds(bounds.x, bounds.y, resizeX, resizeY);
                }else{
                    setPreferredSize(new Dimension(resizeX, resizeY));
                }
                revalidate();
                repaint();
                prevMouse = e.getPoint();
            }
        }
    
        @Override
        public void mouseReleased(MouseEvent e) {
            resizing = false;
            prevMouse = null;
        }
    
        private void updateCursor(Point p) {
            int x = p.x;
            int y = p.y;
            int w = getWidth();
            int h = getHeight();
    
            boolean left = x < RESIZE_MARGIN;
            boolean right = x > w - RESIZE_MARGIN;
            boolean bottom = y > h - RESIZE_MARGIN;
    

            if (bottom && right) {
                cursorType = Cursor.SE_RESIZE_CURSOR;
            } else if (bottom) {
                cursorType = Cursor.S_RESIZE_CURSOR;
            } else if (left && !isFloating) {
                cursorType = Cursor.W_RESIZE_CURSOR;
            } else if (right) {
                cursorType = Cursor.E_RESIZE_CURSOR;
            } else {
                cursorType = Cursor.DEFAULT_CURSOR;
            }
    
            setCursor(Cursor.getPredefinedCursor(cursorType));
        }
    }
    
}
