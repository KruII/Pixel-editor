package com.kruii.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SizeSettings {

    // Współdzielony rozmiar między narzędziami
    private static int toolSize = 1;

    // Panel ustawień współdzielony między narzędziami
    private static final JPanel settingsPanel = new JPanel(new FlowLayout());

    static {
        // Inicjalizacja panelu ustawień
        settingsPanel.add(new JLabel("Rozmiar:"));

        JTextField sizeField = new JTextField(String.valueOf(toolSize), 3);
        sizeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    int val = Integer.parseInt(sizeField.getText().trim());
                    toolSize = Math.max(1, val); // Minimalny rozmiar = 1
                } catch (NumberFormatException ex) {
                    toolSize = 1; // Ustaw wartość domyślną w przypadku błędu
                }
                sizeField.setText(String.valueOf(toolSize)); // Zaktualizuj pole tekstowe
            }
        });
        settingsPanel.add(sizeField);
    }

    // Metoda do pobierania rozmiaru
    public static int getToolSize() {
        return toolSize;
    }

    // Metoda do ustawiania rozmiaru
    public static void setToolSize(int size) {
        toolSize = Math.max(1, size);
    }

    // Metoda do pobierania panelu ustawień
    public static JPanel getSettingsPanel() {
        return settingsPanel;
    }
}
