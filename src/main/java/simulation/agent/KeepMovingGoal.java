package simulation.agent;

import environment.Direction;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentContextInterface;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Goal;

public class KeepMovingGoal extends Goal<Direction> {

    @Override
    public boolean isAchieved(AgentContextInterface<Direction> agentContextInterface) {
        // We always return false here, because, as this is a maintenance goal, we
        // want to always keep pursuing it
        return false;
    }
}
