package traffic;

import core.Direction;
import core.Vehicle;
import core.World;

public class TrafficManager {
    private final World world;
    private final Intersection intersection;

    public TrafficManager(World world, Intersection intersection) {
        this.world = world;
        this.intersection = intersection;
    }

    public boolean spawn(Direction d) {
        world.addVehicle(new Vehicle(d, 400, 300));
        return true;
    }

    public TrafficLight getLight(Direction d) {
        return new TrafficLight(d);
    }
}
