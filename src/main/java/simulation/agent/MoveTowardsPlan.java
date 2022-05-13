package simulation.agent;

import environment.Direction;
import nl.uu.cs.iss.ga.sim2apl.core.agent.PlanToAgentInterface;
import nl.uu.cs.iss.ga.sim2apl.core.plan.PlanExecutionError;
import nl.uu.cs.iss.ga.sim2apl.core.plan.builtin.RunOncePlan;

import java.awt.*;

/**
 * A simple plan to demonstrate how plans work in Sim-2APL.
 *
 * We extend RunOncePlan instead of Plan, so we do not explicitly
 * have to call setFinished(true) at the end of the execute(Once) method.
 */
public class MoveTowardsPlan extends RunOncePlan<Direction> {

    private final MoveTowardsGoal goal;

    public MoveTowardsPlan(MoveTowardsGoal goal) {
        this.goal = goal;
    }

    @Override
    public Direction executeOnce(PlanToAgentInterface<Direction> planToAgentInterface) throws PlanExecutionError {
        AgentBeliefContext context = planToAgentInterface.getContext(AgentBeliefContext.class);
        Point currentPosition = new Point(context.getPosition());

        Direction move = null;

        if (currentPosition.x == goal.getDestination().x) {
            if (currentPosition.y < goal.getDestination().y) {
                currentPosition.y++;
                move = Direction.DOWN;
            } else if (currentPosition.y > goal.getDestination().y) {
                currentPosition.y--;
                move = Direction.UP;
            }
        } else if (currentPosition.x < goal.getDestination().x) {
            currentPosition.x++;
            move = Direction.RIGHT;
        } else {
            currentPosition.x--;
            move = Direction.LEFT;
        }

        // Update our belief about our current position based on the move we expect to make
        context.setPosition(currentPosition);

        // This is collected by Sim-2APL, which sends it to the environment after all agents have
        // decided their move
        return move;
    }
}
