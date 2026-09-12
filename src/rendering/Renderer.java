package rendering;

import java.awt.Color;
import java.awt.Graphics2D;
import core.Route;
import core.Vehicle;
import core.World;

public class Renderer {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public static final Color COLOR_STRAIGHT = new Color(244, 91, 105);
    public static final Color COLOR_LEFT = new Color(113, 183, 255);
    public static final Color COLOR_RIGHT = new Color(255, 202, 53);

    public void render(Graphics2D g, World world) {
        g.fillRect(0, 0, WIDTH, HEIGHT);
        for (Vehicle v : world.getVehicles()) {
            g.setColor(routeColor(null));
            g.fillRect((int) v.getX(), (int) v.getY(), 30, 18);
        }
    }

    public static Color routeColor(Route r) {
        if (r == null) {
            return COLOR_STRAIGHT;
        }
        return switch (r) {
            case LEFT -> COLOR_LEFT;
            case RIGHT -> COLOR_RIGHT;
            default -> COLOR_STRAIGHT;
        };
    }
}
