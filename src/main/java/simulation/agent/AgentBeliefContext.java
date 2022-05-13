package simulation.agent;


import nl.uu.cs.iss.ga.sim2apl.core.agent.Context;

import java.awt.*;
import java.util.Random;

public class AgentBeliefContext implements Context {

    /**
     * By assigning each agent a random object, stochastic decisions of the agent can be seeded
     * for repeated deterministic execution.
     * For stochastic execution, the random object can be initialized without a seed
     */
    private final Random random;

    /**
     * The agent maintains its position in its own belief base.
     * Alternatively, the context could contain a reference to the environment,
     * and the agent can request their current position from the environment whenever
     * necessary
     */
    private Point position;

    /**
     * The agents has some beliefs about the size of the environment.
     */
    private final int environmentWidth;
    private final int environmentHeight;

    public AgentBeliefContext(Random random, Point position, int environmentWidth, int environmentHeight) {
        this.random = random;
        this.position = position;
        this.environmentWidth = environmentWidth;
        this.environmentHeight = environmentHeight;
    }

    public Random getRandom() {
        return random;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public int getEnvironmentWidth() {
        return environmentWidth;
    }

    public int getEnvironmentHeight() {
        return environmentHeight;
    }
}
