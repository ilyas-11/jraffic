package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import traffic.Intersection;

public class World {
    private final Intersection intersection;
    private final List<Vehicle> vehicles = new ArrayList<>();

    public World(Intersection intersection) {
        this.intersection = intersection;
    }

    public Intersection getIntersection() {
        return intersection;
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public List<Vehicle> getVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    public void update(List<Vehicle> permittedVehicles, double frameScale) {
        for (Vehicle vehicle : permittedVehicles) {
            vehicle.move(frameScale);
        }
        vehicles.removeIf(Vehicle::isFinished);
    }

    public void clear() {
        vehicles.clear();
    }
}
