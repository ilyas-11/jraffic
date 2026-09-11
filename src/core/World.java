package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class World {
    private final List<Vehicle> vehicles = new ArrayList<>();

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public List<Vehicle> getVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    public void clear() {
        vehicles.clear();
    }
}
