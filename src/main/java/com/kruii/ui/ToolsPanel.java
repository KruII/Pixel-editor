package com.kruii.ui;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ToolsPanel extends JPanel {

    private final List<Tool> tools = new ArrayList<>();
    private final JPanel settingsPlaceholder;

    private PixelCanvas canvas; // referencja, by zmieniać narzędzie w canvasie
    
        public ToolsPanel(PixelCanvas canvas, JFrame parentFrame) {
            super(new BorderLayout());
            this.canvas = canvas;
    
            // Dodajemy przykładowe narzędzia
            tools.add(new com.kruii.tools.Pencil());
            tools.add(new com.kruii.tools.Brush());
            tools.add(new com.kruii.tools.Eraser());
    
            // Dropdown do wyboru narzędzi
            JComboBox<Tool> combo = new JComboBox<>(tools.toArray(new Tool[0]));
            combo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Tool) {
                        setText(((Tool) value).getName());
                    }
                    return this;
                }
            });
            combo.addActionListener(e -> {
                Tool t = (Tool) combo.getSelectedItem();
                setCurrentTool(t);
            });
            add(combo, BorderLayout.CENTER);
    
            // Placeholder na ustawienia narzędzia
            settingsPlaceholder = new JPanel(new BorderLayout());
            add(settingsPlaceholder, BorderLayout.SOUTH);
    
            // Ustawiamy domyślne narzędzie
            if (!tools.isEmpty()) {
                setCurrentTool(tools.get(0));
                combo.setSelectedIndex(0);
            }
        }
    
        private void setCurrentTool(Tool t) {
            if (t == null) return;
            this.canvas.setTool(t);
    
            // Wymieniamy panel ustawień
            settingsPlaceholder.removeAll();
            JPanel sp = t.getSettingsPanel();
            if (sp != null) {
                settingsPlaceholder.add(sp, BorderLayout.CENTER);
            }
            settingsPlaceholder.revalidate();
            settingsPlaceholder.repaint();
        }
    
        public void setCanvas(PixelCanvas newCanvas) {
            this.canvas = newCanvas;
    }
    
}
