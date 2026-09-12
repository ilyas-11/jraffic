package rendering;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.EnumMap;
import java.util.Map;
import javax.imageio.ImageIO;
import core.Direction;
import core.Route;
import core.Vehicle;
import core.World;
import traffic.Intersection;
import traffic.TrafficManager;

public class Renderer {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public static final Color COLOR_STRAIGHT = new Color(244, 91, 105);
    public static final Color COLOR_LEFT = new Color(113, 183, 255);
    public static final Color COLOR_RIGHT = new Color(255, 202, 53);

    private BufferedImage mapTexture;
    private BufferedImage lightGreen;
    private BufferedImage lightRed;
    private final Map<Route, BufferedImage> carTextures = new EnumMap<>(Route.class);

    public Renderer() {
        loadTextures();
    }

    private void loadTextures() {
        mapTexture = loadImage("assets/map.png", WIDTH, HEIGHT);
        lightGreen = loadImage("assets/light_green.png", 24, 24);
        lightRed = loadImage("assets/light_red.png", 24, 24);
        carTextures.put(Route.STRAIGHT, loadImage("assets/car_red.png", (int) Vehicle.LENGTH, (int) Vehicle.WIDTH));
        carTextures.put(Route.LEFT, loadImage("assets/car_blue.png", (int) Vehicle.LENGTH, (int) Vehicle.WIDTH));
        carTextures.put(Route.RIGHT, loadImage("assets/car_yellow.png", (int) Vehicle.LENGTH, (int) Vehicle.WIDTH));
    }

    private BufferedImage loadImage(String path, int targetWidth, int targetHeight) {
        try {
            BufferedImage raw = ImageIO.read(new File(path));
            if (raw == null) {
                return null;
            }
            BufferedImage optimized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB_PRE);
            Graphics2D g = optimized.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(raw, 0, 0, targetWidth, targetHeight, null);
            g.dispose();
            return optimized;
        } catch (Exception e) {
            return null;
        }
    }

    public void render(Graphics2D g, World world, TrafficManager manager) {
        if (mapTexture != null) {
            g.drawImage(mapTexture, 0, 0, null);
        }

        Intersection intersection = world.getIntersection();
        for (Direction d : Direction.values()) {
            Point2D.Double pos = intersection.lightPosition(d);
            BufferedImage light = manager.getLight(d).isGreen() ? lightGreen : lightRed;
            if (light != null) {
                g.drawImage(light, (int) pos.x - 12, (int) pos.y - 12, null);
            }
        }

        AffineTransform baseTransform = g.getTransform();
        int halfW = (int) Vehicle.LENGTH / 2;
        int halfH = (int) Vehicle.WIDTH / 2;

        for (Vehicle v : world.getVehicles()) {
            BufferedImage texture = carTextures.get(v.getRoute());
            if (texture != null) {
                g.setTransform(baseTransform);
                g.translate(v.getX(), v.getY());
                g.rotate(v.getAngle());
                g.drawImage(texture, -halfW, -halfH, null);
            }
        }
        g.setTransform(baseTransform);
    }

    public static Color routeColor(Route r) {
        if (r == null) {
            return COLOR_STRAIGHT;
        }
        return switch (r) {
            case LEFT -> COLOR_LEFT;
            case RIGHT -> COLOR_RIGHT;
            default -> COLOR_STRAIGHT;
        };
    }
}
