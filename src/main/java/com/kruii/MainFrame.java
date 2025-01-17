package com.kruii;

import javax.swing.*;
import com.kruii.dock.DockManager;
import com.kruii.dock.DockablePanel;
import com.kruii.model.PixelModel;
import com.kruii.ui.PixelCanvas;

import java.awt.*;

public class MainFrame extends JFrame {

    private DockManager dockManager;
    private DockablePanel toolsPanel;
    private JCheckBoxMenuItem showToolsItem;
    private PixelModel model;
    private PixelCanvas canvas;

    public MainFrame() {
        super("Dockable Tools Panel");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Główna zawartość
        setLayout(new BorderLayout());
        model = new PixelModel(64, 64);
        canvas = new PixelCanvas(model);
        

        // Tworzymy menubar z opcją "Show Tools"
        JMenuBar menuBar = new JMenuBar();
        JMenu viewMenu = new JMenu("View");
        showToolsItem = new JCheckBoxMenuItem("Show Tools", true);
        showToolsItem.addActionListener(e -> {
            if (showToolsItem.isSelected()) {
                // Pokaż panel
                // Można od razu zadokować po lewej
                toolsPanel.dockTo(this, BorderLayout.WEST);
            } else {
                // Zamknij/ukryj panel
                toolsPanel.closePanel();
            }
        });
        viewMenu.add(showToolsItem);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);

        // DockManager
        dockManager = new DockManager(this);

        // Tworzymy panel dokowalny
        toolsPanel = new DockablePanel(this, dockManager, showToolsItem, canvas);
        // Domyślnie dokujemy go po lewej
        add(canvas, BorderLayout.CENTER);
        add(toolsPanel, BorderLayout.WEST);

        // Pokaż okno
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        // Wywołanie metody odpowiedzialnej za rysowanie podświetlenia
        dockManager.paintHighlights(g);
    }
}
