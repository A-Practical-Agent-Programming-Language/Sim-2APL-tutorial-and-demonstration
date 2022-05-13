package simulation;

import environment.*;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Agent;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentID;
import nl.uu.cs.iss.ga.sim2apl.core.deliberation.DeliberationResult;
import nl.uu.cs.iss.ga.sim2apl.core.platform.Platform;
import nl.uu.cs.iss.ga.sim2apl.core.step.EnvironmentInterface;
import simulation.agent.MoveFailedTrigger;
import simulation.agent.MoveTowardsGoal;

import java.awt.*;
import java.net.URISyntaxException;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * The ToyGridWorldInterface interfaces Sim-2APL with the ToyGridWorld environment.
 * This allows the Sim-2APL to update the environment by sending the agent actions,
 * and update the agents based on (the consequent) changes in the environment.
 *
 * The EnvironmentInterface registers to the simulation engine to receive events
 * about the progression of the simulation at each time step.
 *
 * Note that for this demo, the environment is implemented in Java, so some methods
 * could be called directly from Sim-2APL agents. However, to properly keep separation
 * of concerns, the EnvironmentInterface should act as a complete wrapper for the environment,
 * regardless of what language the environment is programmed in.
 */
public class ToyGridWorldInterface implements EnvironmentInterface<Direction> {

    private final IToyGridWorld<String> toyGridWorld;
    private final Platform platform;
    private final IEnvironmentView<String> environmentView;
    private final Map<AgentID, String> agentIdToNameMap = new HashMap<>();
    private final int delay;

    public ToyGridWorldInterface(Random random, Platform platform, int width, int height, int delay) {
        this.toyGridWorld = new ToyGridWorld<>(random, width, height);
        this.platform = platform;
        this.environmentView = new EnvironmentConsoleView<>(toyGridWorld);
        this.delay = delay;
    }

    @Override
    public void stepStarting(long l) {
        // Because this demo is relatively simple, we do not have to update the agents before a new time step
        // starts.
        // This method is useful if actions are processed in bulk by the environment, and the environment sends
        // back some information for the agents (not the case here).
    }

    /**
     * The step finished method.
     *
     * For the purpose if this simulation, we do 4 things:
     * 1) We materialize each requested action of the agent in the environment, by calling the environments
     *      move() method
     * 2) We query the agents' goal base to see what their intended destination is
     * 3) We request the IEnvironmentView to visualize the new state of the environment.
     * 4) We let the system sleep for a bit, so we have time to interpret the visualization of the new state of the
     *      environment.
     *
     * @param timeStep The finished time step
     * @param timeStepDuration Computation time taken by the time step (in milliseconds)
     * @param agentActions  An ordered list of futures with the agents' deliberation results. The order in which the
     *                      agents producing these actions were scheduled for execution in the past timestep is
     *                      maintained
     */
    @Override
    public void stepFinished(long timeStep, int timeStepDuration, List<Future<DeliberationResult<Direction>>> agentActions) {
        Map<Point, List<String>> agentDestinations = new HashMap<>();

        for(Future<DeliberationResult<Direction>> deliberationResultFuture : agentActions) {
            try {
                // The deliberation result is a tuple containing the agent ID
                // and an (ordered) list of actions produced by the corresponding agents in the last time step
                DeliberationResult<Direction> deliberationResult = deliberationResultFuture.get();

                // Iterate over all actions and materialize them in the environment
                for(Direction direction : deliberationResult.getActions()) {

                    // We map the AgentID (long uri) to a string for more compact displaying in the environment
                    String agentName = this.agentIdToNameMap.get(deliberationResult.getAgentID());

                    // We get access to this agent's internals, so we can check what goals it is currently pursuing
                    // This allows us to visualize the agents' goals in the environment too
                    Agent agent = platform.getLocalAgent(deliberationResult.getAgentID());
                    addAgentDestination(agentDestinations, agentName, agent);

                    // Actions may fail. If that is the case, the agent should be notified
                    if(!toyGridWorld.move(agentName, direction)) {
                        handleFailure(agent, agentName, direction);
                    }
                }

            } catch (InterruptedException | ExecutionException | URISyntaxException e) {
                e.printStackTrace();
            }
        }

        // Visualize new state of the environment
        environmentView.draw(timeStep, agentDestinations);

        // We sleep for a bit, because the terminal can't keep up with redrawing the state of the environment
        // every time step otherwise
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Iterates over the agent's goals, to see if it is currently pursuing a MoveTowardsGoal.
     * If so, adds the Point representing the coordinate the agent wants to go to the agentDestinations
     * map, so it can be visualized in the environment.
     *
     * @param agentDestinations Map keeping track of all agent's destinations (pursued goals)
     * @param agentName         String representation of the current agent
     * @param agent             Internals of the current agent
     */
    private void addAgentDestination(Map<Point, List<String>> agentDestinations, String agentName, Agent agent) {
        for(Object goal : agent.getGoals()) {
            if (goal instanceof MoveTowardsGoal) {
                Point destination = ((MoveTowardsGoal) goal).getDestination();
                if (!agentDestinations.containsKey(destination)) {
                    agentDestinations.put(destination, new ArrayList<>());
                }
                agentDestinations.get(destination).add(agentName);
            }
        }
    }

    /**
     * Notifies the agent a MOVE action has failed through an external trigger. This allows the agent to update
     * their beliefs about their current position
     *
     * @param agent         Internals of the agent for which a move has failed
     * @param agentName     String representation of the agent for which a move has failed
     * @param direction     Direction of the move that has failed
     */
    private void handleFailure(Agent agent, String agentName, Direction direction) {
        agent.addExternalTrigger(new MoveFailedTrigger(
                toyGridWorld.getPosition(agentName),
                direction
        ));
    }

    @Override
    public void simulationFinished(long l, int i) {
        System.out.println("Done");
    }

    /**
     * Register a new agent to the environment
     * @param agent             Agent to register
     * @param name              Display name of the agent
     * @param initialPosition   Coordinates of the grid location where the agent starts
     */
    public void registerAgent(Agent<Direction> agent, String name, Point initialPosition) {
        this.agentIdToNameMap.put(agent.getAID(), name);
        this.getToyGridWorld().registerAgent(name, initialPosition);
    }

    /**
     * Getter for the ToyGridWorld environment to give Sim-2APL access to useful utility functions.
     * Note that, in some cases, such a getter cannot exist, and all utility methods should be wrapped by
     * the environment interface
     *
     * @return ToyGridWorld instance
     */
    public IToyGridWorld<String> getToyGridWorld() {
        return toyGridWorld;
    }
}
