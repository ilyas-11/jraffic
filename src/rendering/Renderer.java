package rendering;

import java.awt.Graphics2D;
import core.World;

public class Renderer {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public void render(Graphics2D g, World world) {
        g.fillRect(0, 0, WIDTH, HEIGHT);
    }
}
