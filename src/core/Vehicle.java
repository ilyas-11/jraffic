package core;

public class Vehicle {
    public static final double LENGTH = 34.0;
    public static final double WIDTH = 19.0;
    public static final double SPEED = 1.7;

    private final Direction direction;
    private double x;
    private double y;
    private boolean finished;

    public Vehicle(Direction direction, double x, double y) {
        this.direction = direction;
        this.x = x;
        this.y = y;
    }

    public void move() {
        x += direction.getDx() * SPEED;
        y += direction.getDy() * SPEED;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public Direction getDirection() { return direction; }
    public boolean isFinished() { return finished; }
}
