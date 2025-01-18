package com.kruii.utils;

import java.awt.Color;

public class ColorUtils {

    // Prosty alpha-blend
    // alpha=1 -> c1, alpha=0 -> c2
    public static Color blendColors(Color c1, Color c2, float alpha) {
        float r1 = c1.getRed()/255f;
        float g1 = c1.getGreen()/255f;
        float b1 = c1.getBlue()/255f;

        float r2 = c2.getRed()/255f;
        float g2 = c2.getGreen()/255f;
        float b2 = c2.getBlue()/255f;

        float r = alpha*r1 + (1f-alpha)*r2;
        float g = alpha*g1 + (1f-alpha)*g2;
        float b = alpha*b1 + (1f-alpha)*b2;
        return new Color(r, g, b);
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

