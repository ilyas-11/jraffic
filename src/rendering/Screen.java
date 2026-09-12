package rendering;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import javax.swing.JPanel;
import javax.swing.Timer;
import core.World;
import traffic.TrafficManager;

public class Screen extends JPanel {
    private static final long serialVersionUID = 1L;

    private final World world;
    private final TrafficManager manager;
    private final Renderer renderer = new Renderer();
    private final Timer timer;
    private long lastFrameTime;

    public Screen(World world, TrafficManager manager) {
        this.world = world;
        this.manager = manager;

        setPreferredSize(new Dimension(Renderer.WIDTH, Renderer.HEIGHT));
        setBackground(new Color(46, 117, 69));
        setDoubleBuffered(true);
        setFocusable(true);

        installKeys();

        this.timer = new Timer(16, e -> {
            long now = System.nanoTime();
            double frameScale = (now - lastFrameTime) / 16_666_666.0;
            lastFrameTime = now;
            manager.update(Math.min(frameScale, 3.0));
            repaint();
        });
    }

    public void installKeys() {
        new InputHandler(manager).install(this);
    }

    public void start() {
        lastFrameTime = System.nanoTime();
        timer.start();
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics raw) {
        super.paintComponent(raw);
        Graphics2D g = (Graphics2D) raw;
        renderer.render(g, world, manager);
        Toolkit.getDefaultToolkit().sync();
    }
}
