package com.kruii.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ColorSettings {

    private static Color selectedColor = Color.BLACK; // Domyślnie czarny

    // Panel ustawień współdzielony między narzędziami
    private static final JPanel settingsPanel = new JPanel(new FlowLayout());
    private static final JButton colorButton = new JButton("Wybierz kolor");

    static {
        // Ustaw wstępny kolor tła przycisku
        colorButton.setBackground(selectedColor);
        colorButton.setForeground(Color.WHITE);

        colorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(
                        null,
                        "Wybierz kolor",
                        selectedColor
                );
                if (newColor != null) {
                    selectedColor = newColor;
                    colorButton.setBackground(newColor);
                }
            }
        });

        settingsPanel.add(new JLabel("Kolor:"));
        settingsPanel.add(colorButton);
    }

    /**
     * Metoda do pobierania wybranego koloru.
     */
    public static Color getSelectedColor() {
        return selectedColor;
    }

    /**
     * Metoda do ustawiania koloru z zewnątrz (opcjonalnie).
     */
    public static void setSelectedColor(Color color) {
        selectedColor = color;
        colorButton.setBackground(color);
    }

    /**
     * Zwraca panel ustawień (z przyciskiem wyboru koloru).
     */
    public static JPanel getSettingsPanel() {
        return settingsPanel;
    }
}
