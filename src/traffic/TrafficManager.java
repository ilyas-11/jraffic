package traffic;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import core.Direction;
import core.Route;
import core.Vehicle;
import core.World;

public class TrafficManager {
    public static final double SAFETY_GAP = 16.0;
    public static final double MIN_DIST = Vehicle.LENGTH + SAFETY_GAP;

    private static final int MIN_GREEN_TICKS = 120;
    private static final int MAX_GREEN_TICKS = 300;
    private static final int FULL_QUEUE_EXTENSION_TICKS = 180;
    private static final Direction[] SIGNAL_ORDER = {
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST
    };

    private final World world;
    private final Intersection intersection;
    private final Map<Direction, TrafficLight> lights = new EnumMap<>(Direction.class);
    private final Random random = new Random();

    private Direction active = Direction.NORTH;
    private int phaseTicks = 0;
    private boolean activeQueueWasFull;
    private boolean clearingIntersection;

    public TrafficManager(World world, Intersection intersection) {
        this.world = world;
        this.intersection = intersection;
        for (Direction d : Direction.values()) {
            lights.put(d, new TrafficLight(d));
        }
        setActive(active);
    }

    public void update(double frameScale) {
        if (clearingIntersection) {
            if (!hasVehicleToClear()) {
                setActive(nextWaitingDirection());
            }
        } else {
            phaseTicks++;
            activeQueueWasFull |= queueLength(active) >= getLaneCapacity(active);
            if (shouldChangePhase()) {
                beginClearance();
            }
        }

        List<Vehicle> permitted = new ArrayList<>();
        for (Vehicle v : world.getVehicles()) {
            if (canMove(v, frameScale)) {
                permitted.add(v);
            }
        }
        world.update(permitted, frameScale);
    }

    public boolean spawn(Direction d) {
        if (queueLength(d) >= getLaneCapacity(d) || !isSpawnAreaClear(d)) {
            return false;
        }
        Route[] routes = Route.values();
        Route route = routes[random.nextInt(routes.length)];
        world.addVehicle(new Vehicle(d, route, createPath(d, route)));
        return true;
    }

    public boolean spawnRandom() {
        int startIndex = random.nextInt(SIGNAL_ORDER.length);
        for (int i = 0; i < SIGNAL_ORDER.length; i++) {
            Direction target = SIGNAL_ORDER[(startIndex + i) % SIGNAL_ORDER.length];
            if (spawn(target)) {
                return true;
            }
        }
        return false;
    }

    public void clearVehicles() {
        world.clear();
    }

    public TrafficLight getLight(Direction d) {
        return lights.get(d);
    }

    public Direction getActiveDirection() {
        return active;
    }

    public int queueLength(Direction d) {
        int count = 0;
        for (Vehicle v : world.getVehicles()) {
            if (v.getDirection() == d && distanceToStopLine(v) > 0) {
                count++;
            }
        }
        return count;
    }

    public int getLaneCapacity(Direction d) {
        Point2D.Double start = getStartPoint(d);
        double stop = intersection.stopCoordinate(d);
        double length = Math.abs((start.x - stop) * d.getDx() + (start.y - stop) * d.getDy());
        return (int) Math.floor(length / MIN_DIST);
    }

    private boolean shouldChangePhase() {
        if (phaseTicks < MIN_GREEN_TICKS) {
            return false;
        }
        if (queueLength(active) == 0) {
            return true;
        }
        int maxTicks = MAX_GREEN_TICKS + (activeQueueWasFull ? FULL_QUEUE_EXTENSION_TICKS : 0);
        return phaseTicks >= maxTicks;
    }

    private Direction nextWaitingDirection() {
        int activeIndex = Arrays.asList(SIGNAL_ORDER).indexOf(active);
        for (int i = 1; i < SIGNAL_ORDER.length; i++) {
            Direction candidate = SIGNAL_ORDER[(activeIndex + i) % SIGNAL_ORDER.length];
            if (queueLength(candidate) > 0) {
                return candidate;
            }
        }
        return SIGNAL_ORDER[(activeIndex + 1) % SIGNAL_ORDER.length];
    }

    private void setActive(Direction d) {
        active = d;
        phaseTicks = 0;
        activeQueueWasFull = queueLength(d) >= getLaneCapacity(d);
        clearingIntersection = false;
        for (TrafficLight light : lights.values()) {
            light.setGreen(light.getApproach() == d);
        }
    }

    private void beginClearance() {
        clearingIntersection = true;
        for (TrafficLight light : lights.values()) {
            light.setGreen(false);
        }
    }

    private boolean isSpawnAreaClear(Direction d) {
        Point2D.Double start = getStartPoint(d);
        for (Vehicle v : world.getVehicles()) {
            if (v.getDirection() == d && Point2D.distance(start.x, start.y, v.getX(), v.getY()) < MIN_DIST) {
                return false;
            }
        }
        return true;
    }

    private boolean canMove(Vehicle v, double frameScale) {
        if (!hasSafeSpacingAhead(v)) {
            return false;
        }
        double dist = distanceToStopLine(v);
        if (dist <= 0) {
            return true;
        }
        if (clearingIntersection) {
            return false;
        }
        return v.getDirection() == active || dist > Vehicle.SPEED * frameScale;
    }

    private boolean hasSafeSpacingAhead(Vehicle v) {
        Direction d = v.getDirection();
        for (Vehicle other : world.getVehicles()) {
            if (other != v && other.getDirection() == d) {
                boolean isAhead = (other.getX() - v.getX()) * d.getDx() + (other.getY() - v.getY()) * d.getDy() > 0;
                if (isAhead && Point2D.distance(v.getX(), v.getY(), other.getX(), other.getY()) < MIN_DIST) {
                    return false;
                }
            }
        }
        return true;
    }

    private double distanceToStopLine(Vehicle v) {
        Direction d = v.getDirection();
        double stop = intersection.stopCoordinate(d);
        return (stop - v.getX()) * d.getDx() + (stop - v.getY()) * d.getDy();
    }

    private boolean hasVehicleToClear() {
        double safetyMargin = Vehicle.LENGTH / 2.0;
        for (Vehicle v : world.getVehicles()) {
            if (isInIntersection(v, 0.0)) {
                return true;
            }
            if (v.getDirection() == active && distanceToStopLine(v) <= 0 && isInIntersection(v, safetyMargin)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInIntersection(Vehicle v, double margin) {
        double limit = Intersection.HALF_SIZE + margin;
        return Math.abs(v.getX() - intersection.getCenterX()) <= limit
                && Math.abs(v.getY() - intersection.getCenterY()) <= limit;
    }

    private Point2D.Double getStartPoint(Direction d) {
        double cx = intersection.getCenterX();
        double cy = intersection.getCenterY();
        double offset = Intersection.LANE_OFFSET;

        return switch (d) {
            case NORTH -> new Point2D.Double(cx + offset, 585);
            case SOUTH -> new Point2D.Double(cx - offset, 15);
            case EAST -> new Point2D.Double(15, cy + offset);
            case WEST -> new Point2D.Double(785, cy - offset);
        };
    }

    private List<Point2D.Double> createPath(Direction d, Route route) {
        double cx = intersection.getCenterX();
        double cy = intersection.getCenterY();
        double offset = Intersection.LANE_OFFSET;

        List<Point2D.Double> path = new ArrayList<>();
        path.add(getStartPoint(d));

        Direction targetDir = switch (route) {
            case STRAIGHT -> d;
            case RIGHT -> d.right();
            case LEFT -> d.left();
        };

        Point2D.Double exitPoint = switch (targetDir) {
            case NORTH -> new Point2D.Double(cx + offset, -50);
            case SOUTH -> new Point2D.Double(cx - offset, 650);
            case EAST -> new Point2D.Double(850, cy + offset);
            case WEST -> new Point2D.Double(-50, cy - offset);
        };

        if (route == Route.RIGHT) {
            path.add(switch (d) {
                case NORTH -> new Point2D.Double(cx + offset, cy + offset);
                case SOUTH -> new Point2D.Double(cx - offset, cy - offset);
                case EAST -> new Point2D.Double(cx - offset, cy + offset);
                case WEST -> new Point2D.Double(cx + offset, cy - offset);
            });
        } else if (route == Route.LEFT) {
            switch (d) {
                case NORTH -> {
                    path.add(new Point2D.Double(cx + offset, cy + offset));
                    path.add(new Point2D.Double(cx - offset, cy - offset));
                }
                case SOUTH -> {
                    path.add(new Point2D.Double(cx - offset, cy - offset));
                    path.add(new Point2D.Double(cx + offset, cy + offset));
                }
                case EAST -> {
                    path.add(new Point2D.Double(cx - offset, cy + offset));
                    path.add(new Point2D.Double(cx + offset, cy - offset));
                }
                case WEST -> {
                    path.add(new Point2D.Double(cx + offset, cy - offset));
                    path.add(new Point2D.Double(cx - offset, cy + offset));
                }
            }
        }

        path.add(exitPoint);
        return path;
    }
}
