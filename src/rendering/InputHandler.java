package rendering;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import core.Direction;
import traffic.TrafficManager;

public class InputHandler {
    private final TrafficManager manager;

    public InputHandler(TrafficManager manager) {
        this.manager = manager;
    }

    public void install(JComponent component) {
        InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = component.getActionMap();

        bind(inputMap, actionMap, "UP", "spawn-north", () -> manager.spawn(Direction.NORTH));
        bind(inputMap, actionMap, "DOWN", "spawn-south", () -> manager.spawn(Direction.SOUTH));
        bind(inputMap, actionMap, "RIGHT", "spawn-east", () -> manager.spawn(Direction.EAST));
        bind(inputMap, actionMap, "LEFT", "spawn-west", () -> manager.spawn(Direction.WEST));

        bind(inputMap, actionMap, "R", "spawn-random", manager::spawnRandom);
        bind(inputMap, actionMap, "r", "spawn-random", manager::spawnRandom);

        bind(inputMap, actionMap, "C", "clear-vehicles", manager::clearVehicles);
        bind(inputMap, actionMap, "c", "clear-vehicles", manager::clearVehicles);

        bind(inputMap, actionMap, "ESCAPE", "exit-app", () -> System.exit(0));
    }

    private void bind(InputMap inputMap, ActionMap actionMap, String key, String name, Runnable action) {
        inputMap.put(KeyStroke.getKeyStroke(key), name);
        actionMap.put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }
}
