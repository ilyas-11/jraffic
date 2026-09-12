package traffic;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import core.Direction;
import core.Route;
import core.Vehicle;
import core.World;

public class TrafficManager {
    private final World world;
    private final Intersection intersection;
    private final Map<Direction, TrafficLight> lights = new EnumMap<>(Direction.class);
    private Direction active = Direction.NORTH;

    public TrafficManager(World world, Intersection intersection) {
        this.world = world;
        this.intersection = intersection;
        for (Direction d : Direction.values()) {
            lights.put(d, new TrafficLight(d));
        }
    }

    public boolean spawn(Direction d) {
        Route[] routes = Route.values();
        Route route = routes[new Random().nextInt(routes.length)];
        world.addVehicle(new Vehicle(d, 400, 300));
        return true;
    }

    public TrafficLight getLight(Direction d) {
        return lights.get(d);
    }
}
