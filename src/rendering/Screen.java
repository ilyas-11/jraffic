package rendering;

import javax.swing.JPanel;
import java.awt.Dimension;
import core.World;

public class Screen extends JPanel {
    private final World world;
    private final Renderer renderer = new Renderer();

    public Screen(World world) {
        this.world = world;
        setPreferredSize(new Dimension(Renderer.WIDTH, Renderer.HEIGHT));
    }
}
