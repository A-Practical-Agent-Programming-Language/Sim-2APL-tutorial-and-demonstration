package simulation.agent;

import environment.Direction;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Trigger;

import java.awt.*;

/**
 * An external trigger that can be sent to the agent by the environment if a MOVE action failed.
 * The agent should update their beliefs based on this information
 */
public class MoveFailedTrigger implements Trigger {

    private final Point currentPosition;
    private final Direction failedMove;

    public MoveFailedTrigger(Point currentPosition, Direction failedMove) {
        this.currentPosition = currentPosition;
        this.failedMove = failedMove;
    }

    public Point getCurrentPosition() {
        return currentPosition;
    }

    public Direction getFailedMove() {
        return failedMove;
    }
}
