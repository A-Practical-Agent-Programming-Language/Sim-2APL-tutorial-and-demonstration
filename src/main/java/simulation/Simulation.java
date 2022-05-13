package simulation;

import environment.Direction;
import net.sourceforge.argparse4j.inf.Namespace;
import nl.uu.cs.iss.ga.sim2apl.core.agent.Agent;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentArguments;
import nl.uu.cs.iss.ga.sim2apl.core.defaults.messenger.DefaultMessenger;
import nl.uu.cs.iss.ga.sim2apl.core.platform.Platform;
import nl.uu.cs.iss.ga.sim2apl.core.step.DefaultSimulationEngine;
import nl.uu.cs.iss.ga.sim2apl.core.step.SimulationEngine;
import simulation.agent.AgentBeliefContext;
import simulation.agent.ExternalTriggerPlanScheme;
import simulation.agent.GoalPlanScheme;
import simulation.agent.KeepMovingGoal;
import util.Constants;

import java.awt.*;
import java.net.URISyntaxException;
import java.util.Random;

public class Simulation {

    /**
     * The Platform is a 2APL class that organizes and executes all the agents.
     * Typically, each compute node is instantiated with one platform.
     */
    private Platform platform;

    /**
     * We create an interface between Sim-2APL and the ToyGridWorld environment, so the environment can be updated
     * after each time step.
     * The environment interface subscribes to time step events broadcast by Sim-2APL.
     */
    private ToyGridWorldInterface environmentInterface;

    /**
     * The simulation engine 'drives' the simulation. The easiest is to use the default simulation engine,
     * which just iterates over the specified number of time steps, or iterates indefinitely if no fixed
     * number of time steps is specified.
     *
     * Note that we specify the class type of actions agents can perform. In this example, the actions
     * are just directions in the environment.
     */
    SimulationEngine<Direction> simulationEngine;

    public Simulation(Namespace ns) {
        // The platform serves as the container for all agents on this computer
        platform = Platform.newPlatform(
                4, // Use 4 threads for concurrent execution of agents
                new DefaultMessenger<Direction>() // Required for platform creation, but not used here
        );

        // Use a seed if specified, otherwise, create a new random object without a seed
        Integer randomSeed = ns.getInt(Constants.ARG_SEED);
        Random random;
        if (randomSeed == null) {
            random = new Random();
        } else {
             random = new Random(randomSeed);
        }

        // The environment interface allows Sim-2APL to effect the agent's actions in the environment,
        // and pass information from the environment back to the agents.
        environmentInterface = new ToyGridWorldInterface(
                new Random(random.nextLong()),
                platform,
                ns.getInt(Constants.ARG_WIDTH),
                ns.getInt(Constants.ARG_HEIGHT),
                ns.getInt(Constants.ARG_BETWEEN_STEP_DELAY)
        );

        // We can pass this (and any other) environment interface implementation to the constructor of the
        // simulation engine to automatically register it as a subscriber
        // The simulation engine is what makes sure all the steps are run
        simulationEngine = new DefaultSimulationEngine<>(platform, environmentInterface);

        // Create some agents for this demonstration
        tryCreateAgents(ns.getInt(Constants.ARG_N_AGENTS), random);

        // We start the simulation once all agents are ready
        simulationEngine.start();
    }

    /**
     * Creates some agents
     * @param nAgents   Number of agents to instantiate
     * @param random    (optionally seeded) random object for stochastic agent decision-making
     */
    private void tryCreateAgents(int nAgents, Random random) {
        int i = 0;
        while (i < nAgents) {
            try {
                createAgent(Integer.toString(i), random.nextInt());
                i++;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create a single agent.
     *
     * An agent is constructed through its AgentArguments, to which all contexts (representing beliefs) and plan
     * schemes are added.
     *
     * After the agent has been created (and automatically registered to the platform), we can add its initial goals,
     * and place it in the environment
     *
     * @param name  Display name of the agent for representation in the environment
     * @param seed  The random seed for stochastic decision-making that the agent will use
     * @throws URISyntaxException   Should not be thrown
     */
    private void createAgent(String name, int seed) throws URISyntaxException {
        AgentArguments<Direction> arguments = new AgentArguments<>();
        arguments.addExternalTriggerPlanScheme(new ExternalTriggerPlanScheme());
        arguments.addGoalPlanScheme(new GoalPlanScheme());

        Point initialPosition = environmentInterface.getToyGridWorld().getRandomFreePoint(false);
        AgentBeliefContext beliefContext = new AgentBeliefContext(
                new Random(seed),
                initialPosition,
                environmentInterface.getToyGridWorld().getWidth(),
                environmentInterface.getToyGridWorld().getHeight()
        );

        arguments.addContext(beliefContext);
        Agent<Direction> agent = new Agent<>(platform, arguments);

        // We just adopt this goal, and from now on, the agent will pick random destinations to move towards!
        agent.adoptGoal(new KeepMovingGoal());

        environmentInterface.registerAgent(agent, name, initialPosition);
    }

}
