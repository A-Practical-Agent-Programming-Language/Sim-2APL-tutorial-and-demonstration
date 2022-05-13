package simulation.agent;

import environment.Direction;
import nl.uu.cs.iss.ga.sim2apl.core.agent.PlanToAgentInterface;
import nl.uu.cs.iss.ga.sim2apl.core.plan.Plan;
import nl.uu.cs.iss.ga.sim2apl.core.plan.PlanExecutionError;

import java.awt.*;

public class KeepMovingPlan extends Plan<Direction> {

    @Override
    public Direction execute(PlanToAgentInterface<Direction> planToAgentInterface) throws PlanExecutionError {

        // Check no other MoveTowardsGoal is currently being pursued
        if (!planToAgentInterface.hasGoal(MoveTowardsGoal.class)) {

            // Pick a new destination
            Point newDestination = sampleNewPoint(planToAgentInterface);

            // Create a goal to move to that destination
            MoveTowardsGoal newGoal = new MoveTowardsGoal(newDestination);

            // Adopt the new goal, so the agent starts pursuing it
            planToAgentInterface.adoptGoal(newGoal);
        }

        // The current plan only performs internal actions. No need to return any actions to the environment
        return null;
    }

    /**
     * Lets the agent determine a new random destination within the environment grid
     * @param planToAgentInterface planToAgentInterface
     * @return Random point in the grid environment
     */
    private Point sampleNewPoint(PlanToAgentInterface<Direction> planToAgentInterface) {
        AgentBeliefContext context = planToAgentInterface.getContext(AgentBeliefContext.class);
        int newDestinationX = context.getRandom().nextInt(context.getEnvironmentWidth());
        int newDestinationY = context.getRandom().nextInt(context.getEnvironmentHeight());
        return new Point(newDestinationX, newDestinationY);
    }
}
