package com.kruii.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ColorSettings {

    // Współdzielony rozmiar między narzędziami
    private static int colorValue = 200; // od 0..255 (im więcej, tym jaśniejszy)

    // Panel ustawień współdzielony między narzędziami
    private static final JPanel settingsPanel = new JPanel(new FlowLayout());

    static {
        // Inicjalizacja panelu ustawień
        settingsPanel.add(new JLabel("Kolor(0-255):"));

        JTextField colorField = new JTextField(String.valueOf(colorValue), 3);
        colorField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    int val = Integer.parseInt(colorField.getText().trim());
                    colorValue = Math.min(255, Math.max(0, val));
                } catch (NumberFormatException ex) {
                    colorValue = 1; // Ustaw wartość domyślną w przypadku błędu
                }
                colorField.setText(String.valueOf(colorValue)); // Zaktualizuj pole tekstowe
            }
        });
        settingsPanel.add(colorField);
    }

    // Metoda do pobierania rozmiaru
    public static int getToolValue() {
        return colorValue;
    }

    // Metoda do ustawiania rozmiaru
    public static void setToolValue(int size) {
        colorValue = Math.max(0, size);
    }

    // Metoda do pobierania panelu ustawień
    public static JPanel getSettingsPanel() {
        return settingsPanel;
    }
}
