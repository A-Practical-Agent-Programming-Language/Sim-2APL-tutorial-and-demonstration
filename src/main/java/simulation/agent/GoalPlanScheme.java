package simulation.agent;

import environment.Direction;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentContextInterface;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Trigger;
import nl.uu.cs.iss.ga.sim2apl.core.plan.Plan;
import nl.uu.cs.iss.ga.sim2apl.core.plan.PlanScheme;

public class GoalPlanScheme implements PlanScheme<Direction> {

    @Override
    public Plan<Direction> instantiate(Trigger trigger, AgentContextInterface<Direction> agentContextInterface) {

        if (trigger instanceof MoveTowardsGoal) {
            return new MoveTowardsPlan((MoveTowardsGoal) trigger);
        } else if (trigger instanceof KeepMovingGoal) {
            return new KeepMovingPlan();
        }

        return null;
    }
}
