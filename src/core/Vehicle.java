package core;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Vehicle {
    public static final double LENGTH = 34.0;
    public static final double WIDTH = 19.0;
    public static final double SPEED = 1.7;

    private final Direction direction;
    private final Route route;
    private final List<Point2D.Double> path;
    private int nextPoint = 1;
    private double x;
    private double y;
    private boolean finished;

    public Vehicle(Direction direction, Route route, List<Point2D.Double> path) {
        this.direction = direction;
        this.route = route;
        this.path = Collections.unmodifiableList(new ArrayList<>(path));
        Point2D.Double start = path.get(0);
        this.x = start.x;
        this.y = start.y;
    }

    public void move(double frameScale) {
        if (finished || nextPoint >= path.size()) {
            finished = true;
            return;
        }

        double remaining = SPEED * frameScale;
        while (remaining > 0 && !finished) {
            Point2D.Double target = path.get(nextPoint);
            double dx = target.x - x;
            double dy = target.y - y;
            double dist = Math.hypot(dx, dy);

            if (dist <= remaining) {
                x = target.x;
                y = target.y;
                remaining -= dist;
                nextPoint++;
                if (nextPoint >= path.size()) {
                    finished = true;
                }
            } else {
                x += (dx / dist) * remaining;
                y += (dy / dist) * remaining;
                remaining = 0;
            }
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Direction getDirection() {
        return direction;
    }

    public Route getRoute() {
        return route;
    }

    public boolean isFinished() {
        return finished;
    }

    public double getAngle() {
        Point2D.Double target = nextPoint < path.size() ? path.get(nextPoint) : path.get(path.size() - 1);
        return Math.atan2(target.y - y, target.x - x);
    }
}
