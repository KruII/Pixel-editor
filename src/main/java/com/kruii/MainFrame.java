package com.kruii;

import javax.swing.*;
import com.kruii.dock.DockManager;
import com.kruii.dock.DockablePanel;
import com.kruii.model.PixelModel;
import com.kruii.ui.PixelCanvas;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    private DockManager dockManager;
    private DockablePanel toolsPanel;
    private JCheckBoxMenuItem showToolsItem;
    private PixelModel model;
    private PixelCanvas canvas;
    
    private JMenuItem fileNew, fileLoad, fileSave, fileExport;
    private JMenu viewportMenu;

    // Lista wszystkich workspace'ów (Canvasów) i wskaźnik na obecnie wybrany
    private final List<PixelCanvas> workspaceList = new ArrayList<>();
    private int workspaceCounter = 1; // numeracja workspace'ów startuje od 1

    public MainFrame() {
        super("Dockable Tools Panel");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Główna zawartość
        setLayout(new BorderLayout());

        // Tworzymy domyślny model 16x16
        model = new PixelModel(16, 16);
        canvas = new PixelCanvas(model);
        
        // Dodajemy pierwszy workspace do listy
        workspaceList.add(canvas);

        // Tworzymy menubar
        JMenuBar menuBar = new JMenuBar();
        
        // --- Menu File ---
        JMenu fileMenu = new JMenu("File");
        fileNew = new JMenuItem("New");
        fileLoad = new JMenuItem("Load");
        fileSave = new JMenuItem("Save");
        fileExport = new JMenuItem("Export");
        
        // Akcja dla "New"
        fileNew.addActionListener(e -> openNewDialog());

        fileMenu.add(fileNew);
        fileMenu.add(fileLoad);
        fileMenu.add(fileSave);
        fileMenu.add(fileExport);

        // --- Menu View (Tools) ---
        JMenu viewMenu = new JMenu("View");
        showToolsItem = new JCheckBoxMenuItem("Show Tools", true);
        showToolsItem.addActionListener(e -> {
            if (showToolsItem.isSelected()) {
                // Pokaż/dokuj panel
                toolsPanel.dockTo(this, BorderLayout.WEST);
            } else {
                // Zamknij/ukryj panel
                toolsPanel.closePanel();
            }
        });
        viewMenu.add(showToolsItem);

        // --- Menu Viewport (Workspace) ---
        viewportMenu = new JMenu("Viewport");
        // Dodajemy pierwszą domyślną pozycję w menu (Workspace 1)
        addWorkspaceMenuItem(0); 
        
        // Składamy pasek menu
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(viewportMenu);
        setJMenuBar(menuBar);

        // DockManager
        dockManager = new DockManager(this);

        // Tworzymy panel dokowalny (Tools)
        toolsPanel = new DockablePanel(this, dockManager, showToolsItem, canvas);

        // Domyślnie dokujemy panel narzędziowy po lewej, a canvas dajemy na środek
        add(canvas, BorderLayout.CENTER);
        add(toolsPanel, BorderLayout.WEST);

        // Pokaż okno
        setVisible(true);
    }

    /**
     * Otwiera okno dialogowe do utworzenia nowego workspace'u.
     */
    private void openNewDialog() {
        // Tworzymy dialog modalny
        JDialog dialog = new JDialog(this, "New Workspace", true);
        dialog.setSize(250, 150);
        dialog.setLayout(new GridLayout(3, 2, 5, 5));
        dialog.setLocationRelativeTo(this);

        // Etykiety
        JLabel widthLabel = new JLabel("Width:");
        JLabel heightLabel = new JLabel("Height:");

        // Spinnery do wyboru szerokości i wysokości
        JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(16, 1, 9999, 1));
        JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel(16, 1, 9999, 1));

        // Przyciski OK i Cancel
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        // Panel na przyciski
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Dodajemy elementy do dialogu
        dialog.add(widthLabel);
        dialog.add(widthSpinner);
        dialog.add(heightLabel);
        dialog.add(heightSpinner);
        dialog.add(new JLabel()); // puste miejsce w siatce
        dialog.add(buttonPanel);

        // Akcja po kliknięciu OK
        okButton.addActionListener(e -> {
            int w = (int) widthSpinner.getValue();
            int h = (int) heightSpinner.getValue();
            dialog.dispose();
            createNewWorkspace(w, h);
        });

        // Akcja po kliknięciu Cancel
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    /**
     * Tworzy nowy PixelModel i PixelCanvas o zadanych wymiarach,
     * dodaje do listy workspace'ów i do menu "Viewport".
     */
    private void createNewWorkspace(int width, int height) {
        PixelModel newModel = new PixelModel(width, height);
        PixelCanvas newCanvas = new PixelCanvas(newModel);
        
        // Dodajemy do listy workspace'ów
        workspaceList.add(newCanvas);

        // Dodajemy pozycję w menu "Viewport"
        addWorkspaceMenuItem(workspaceList.size() - 1);
    }

    /**
     * Dodaje do menu Viewport nową pozycję "Workspace X" i ustawia obsługę kliknięcia.
     * @param index index w liście workspaceList
     */
    private void addWorkspaceMenuItem(int index) {
        // Numer workspace'a (czysto informacyjny - np. 1,2,3,...)
        // Możemy też użyć `workspaceList.size()`; 
        // ale często zwiększa się osobno (workspaceCounter).
        String title = "Workspace " + workspaceCounter;
        workspaceCounter++;

        JMenuItem item = new JMenuItem(title);
        item.addActionListener(e -> switchWorkspace(index));
        viewportMenu.add(item);
    }

    /**
     * Przełącza aktualnie wyświetlany canvas na ten o danym indeksie z listy workspaceList.
     */
    private void switchWorkspace(int index) {
        // Usuwamy stary canvas
        getContentPane().remove(canvas);
    
        // Pobieramy nowy canvas z listy
        PixelCanvas newCanvas = workspaceList.get(index);
        this.canvas = newCanvas;
    
        // Dodajemy nowy na środek
        add(canvas, BorderLayout.CENTER);
    
        // -- TU NAJWAŻNIEJSZA RZECZ --
        // Powiadamiamy ToolsPanel o zmianie canvasa:
        toolsPanel.setCanvas(newCanvas);
    
        // Odświeżenie interfejsu
        revalidate();
        repaint();
    }
    

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        // Wywołanie metody odpowiedzialnej za rysowanie podświetlenia z DockManager
        dockManager.paintHighlights(g);
    }
}
