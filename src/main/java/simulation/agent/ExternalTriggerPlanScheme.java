package simulation.agent;

import environment.Direction;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentContextInterface;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Trigger;
import nl.uu.cs.iss.ga.sim2apl.core.plan.Plan;
import nl.uu.cs.iss.ga.sim2apl.core.plan.PlanScheme;

public class ExternalTriggerPlanScheme implements PlanScheme<Direction> {

    @Override
    public Plan<Direction> instantiate(Trigger trigger, AgentContextInterface<Direction> agentContextInterface) {

        if (trigger instanceof MoveFailedTrigger) {
            return new UpdatePositionPlan((MoveFailedTrigger) trigger);
        }

        return null;
    }
}
