package environment;

import java.awt.*;
import java.util.List;
import java.util.Map;

public interface IEnvironmentView<A> {

    /**
     * Draw the current state of the environment
     *
     * @param timeStep  Current time step t
     * @param targets   A map containing points that agents are pursuing as their destination goals.
     *                  If a point is pursued by at least one agent, it should exist as a key in the map,
     *                  and the value should be a list of all agent identifiers pursuing that goal.
     *
     *                  This may be an empty map to ignore drawing agent goals
     */
    void draw(long timeStep, Map<Point, List<A>> targets);
}
