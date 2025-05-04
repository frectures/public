package sandbox;

public class Pythagoras {
    public static double distance(double x1, double y1,
                                  double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double square = dx * dx + dy * dy;
        return Math.sqrt(square);
    }
}
