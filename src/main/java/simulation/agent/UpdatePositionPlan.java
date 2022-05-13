package simulation.agent;

import environment.Direction;
import nl.uu.cs.iss.ga.sim2apl.core.agent.PlanToAgentInterface;
import nl.uu.cs.iss.ga.sim2apl.core.plan.PlanExecutionError;
import nl.uu.cs.iss.ga.sim2apl.core.plan.builtin.RunOncePlan;

public class UpdatePositionPlan extends RunOncePlan<Direction> {

    private final MoveFailedTrigger trigger;

    public UpdatePositionPlan(MoveFailedTrigger trigger) {
        this.trigger = trigger;
    }

    @Override
    public Direction executeOnce(PlanToAgentInterface<Direction> planToAgentInterface) throws PlanExecutionError {
        AgentBeliefContext context = planToAgentInterface.getContext(AgentBeliefContext.class);
        context.setPosition(trigger.getCurrentPosition());
        return null;
    }
}
