import javax.swing.JFrame;
import core.World;
import rendering.Screen;
import traffic.Intersection;
import traffic.TrafficManager;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        Intersection intersection = new Intersection(400, 300);
        World world = new World();
        TrafficManager manager = new TrafficManager(world, intersection);
        Screen screen = new Screen(world);

        JFrame window = new JFrame("jraffic");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(screen);
        window.pack();
        window.setVisible(true);
    }
}
