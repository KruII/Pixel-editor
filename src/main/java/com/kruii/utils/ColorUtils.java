package com.kruii.utils;

import java.awt.Color;

public class ColorUtils {

    // Prosty alpha-blend
    // alpha=1 -> c1, alpha=0 -> c2
    public static Color blendColors(Color fg, Color bg, float alpha) {
        // alpha = 1.0 => weź w 100% kolor fg
        // alpha = 0.0 => weź w 100% kolor bg
    
        // Składniki pierwszego koloru
        float r1 = fg.getRed()   / 255f;
        float g1 = fg.getGreen() / 255f;
        float b1 = fg.getBlue()  / 255f;
        float a1 = fg.getAlpha() / 255f; // uwzględniamy także alpha fg
    
        // Składniki drugiego (tła)
        float r2 = bg.getRed()   / 255f;
        float g2 = bg.getGreen() / 255f;
        float b2 = bg.getBlue()  / 255f;
        float a2 = bg.getAlpha() / 255f; // uwzględniamy alpha tła
    
        // Najprostszy alpha-blend (źródło over destination)
        // Wersja: outAlpha = a1*alpha + a2*(1-alpha)
        float outA = a1 * alpha + a2 * (1 - alpha);
    
        // Jeśli outA > 0, to liczymy wartości R,G,B
        // (uwaga na ewentualne dzielenie przez zero)
        float outR = 0, outG = 0, outB = 0;
        if (outA > 0) {
            outR = (r1 * (a1 * alpha) + r2 * (a2 * (1 - alpha))) / outA;
            outG = (g1 * (a1 * alpha) + g2 * (a2 * (1 - alpha))) / outA;
            outB = (b1 * (a1 * alpha) + b2 * (a2 * (1 - alpha))) / outA;
        }
    
        return new Color(outR, outG, outB, outA);
    }
    

    public static int findClosestColorInPalette(Color c, Color[] palette) {
        int bestIndex = 0;
        double bestDist = Double.MAX_VALUE;
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        for (int i = 0; i < palette.length; i++) {
            Color pc = palette[i];
            int dr = r - pc.getRed();
            int dg = g - pc.getGreen();
            int db = b - pc.getBlue();
            double dist = dr*dr + dg*dg + db*db;
            if(dist < bestDist) {
                bestDist = dist;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

}

