package traffic;

import java.awt.geom.Point2D;
import core.Direction;

public class Intersection {
    public static final double HALF_SIZE = 70.0;
    public static final double LANE_OFFSET = 24.0;
    public static final double STOP_MARGIN = 16.0;

    private final double centerX;
    private final double centerY;

    public Intersection(double centerX, double centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public double stopCoordinate(Direction direction) {
        return switch (direction) {
            case NORTH -> centerY + HALF_SIZE + STOP_MARGIN;
            case SOUTH -> centerY - HALF_SIZE - STOP_MARGIN;
            case EAST -> centerX - HALF_SIZE - STOP_MARGIN;
            case WEST -> centerX + HALF_SIZE + STOP_MARGIN;
        };
    }

    public Point2D.Double lightPosition(Direction direction) {
        double offset = 14.0;
        return switch (direction) {
            case NORTH -> new Point2D.Double(centerX + HALF_SIZE + offset, centerY + HALF_SIZE + offset);
            case SOUTH -> new Point2D.Double(centerX - HALF_SIZE - offset, centerY - HALF_SIZE - offset);
            case EAST -> new Point2D.Double(centerX - HALF_SIZE - offset, centerY + HALF_SIZE + offset);
            case WEST -> new Point2D.Double(centerX + HALF_SIZE + offset, centerY - HALF_SIZE - offset);
        };
    }
}
