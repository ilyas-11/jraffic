package traffic;

import core.Direction;

public class TrafficLight {
    private final Direction approach;
    private boolean green;

    public TrafficLight(Direction approach) {
        this.approach = approach;
    }

    public Direction getApproach() {
        return approach;
    }

    public boolean isGreen() {
        return green;
    }

    public void setGreen(boolean green) {
        this.green = green;
    }
}
