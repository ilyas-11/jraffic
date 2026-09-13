import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import core.World;
import rendering.Screen;
import traffic.Intersection;
import traffic.TrafficManager;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Intersection intersection = new Intersection(400, 300);
            World world = new World(intersection);
            TrafficManager manager = new TrafficManager(world, intersection);
            Screen screen = new Screen(world, manager);

            JFrame window = new JFrame("Jraffic : traffic control simulation");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setContentPane(screen);
            window.pack();
            window.setLocationRelativeTo(null);
            window.setResizable(false);
            window.setVisible(true);

            screen.start();
        });
    }
}
