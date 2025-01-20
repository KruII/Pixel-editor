package com.kruii.utils;

public class Circle {

    public static int[][] getPixelCircleBorder(int width,int height,int pixelSize){
        return getPixelCircleBorder(width, height, pixelSize, 1.0);
    }
    /**
     * Metoda zwraca Shape reprezentujący obrys pikselowego okręgu (tylko krawędź).
     * Uwzględnia przypadki parzystych i nieparzystych wymiarów, aby uniknąć
     * problemów typu "+1" przy rysowaniu.
     *
     * 
     * @param width    szerokość obszaru koła (średnica albo minimalny wymiar)
     * @param height   wysokość obszaru koła (średnica albo minimalny wymiar)
     * @param zoom     współczynnik powiększenia: ile pikseli na 1 jednostkę logiki
     *
     * @return         obiekt Shape (Path2D), który składa się z małych kwadracików 
     *                 wyłącznie w obszarze obrysu koła.
     */
    public static int[][] getPixelCircleBorder(
            int width,
            int height,
            int pixelSize,
            double tolerance
    ) {

        double rx = width / 2.0;
        double ry = height / 2.0;

        int[][] data = new int[height][width];

        for (int py = 0; py < height; py++) {
            // Odległość w osi Y od środka
            double dy = py - (height - 1) / 2.0; // Środek to (height - 1) / 2
        
            for (int px = 0; px < width; px++) {
                // Odległość w osi X od środka
                double dx = px - (width - 1) / 2.0; // Środek to (width - 1) / 2
        
                // Równanie elipsy: (x^2 / rx^2) + (y^2 / ry^2)
                double ellipseEq = (dx * dx) / (rx * rx) + (dy * dy) / (ry * ry);
        
                // Sprawdzamy, czy punkt mieści się w elipsie (<= 1)
                if (ellipseEq <= tolerance) {
                    data[py][px] = 1; // Wewnątrz elipsy
                } else {
                    data[py][px] = 0; // Poza elipsą
                }
            }
        }
        

        // Zwracamy cały obrys (złożony z pikseli)
        return data;
    }
}
