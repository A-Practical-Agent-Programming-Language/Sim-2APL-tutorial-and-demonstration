package simulation.agent;

import environment.Direction;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentContextInterface;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Goal;

import java.awt.*;

public class MoveTowardsGoal extends Goal<Direction> {

    private final Point destination;

    public MoveTowardsGoal(Point destination) {
        this.destination = destination;
    }

    public Point getDestination() {
        return destination;
    }

    @Override
    public boolean isAchieved(AgentContextInterface<Direction> agentContextInterface) {
        AgentBeliefContext context = agentContextInterface.getContext(AgentBeliefContext.class);
        // The goal is achieved if the agent beliefs their current position is the goal position
        return this.destination.equals(context.getPosition());
    }
}
