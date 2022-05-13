package environment;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * A toy 2D grid world environment in which agents can move around and not much else
 * @param <A>   The generic type with which the agents will be represented
 */
public class ToyGridWorld<A> implements IToyGridWorld<A> {

    /**
     * By using a random object, stochastic updates of the environment can be seeded
     * for repeated deterministic execution.
     * For stochastic execution, the random object can be initialized without a seed
     */
    private final Random random;

    private final int width;
    private final int height;

    // Each first array represents a row (y), each nested array represents a column (x)
    // The (0,0) coordinate is in the top left corner
    private final List<List<A>> grid;
    private final Map<A, Point> agentLocations;

    /**
     * Instantiate a new ToyGridWorld
     *
     * @param random    Random object for stochastic decisions
     * @param width     Width of the grid world
     * @param height    Height of the grid world
     */
    public ToyGridWorld(Random random, int width, int height) {
        this.random = random;
        this.width = width;
        this.height = height;
        this.grid = buildGrid();
        this.agentLocations = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWidth() {
        return this.width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHeight() {
        return this.height;
    }

    /**
     * Constructs a new grid with the specified dimensions
     * @return  The new grid
     */
    private List<List<A>> buildGrid() {
        List<List<A>> grid = new ArrayList<>();
        for(int y = 0; y < this.height; y++) {
            grid.add(y, new ArrayList<>());
            for(int x = 0; x < this.width; x++) {
                grid.get(y).add(x, null);
            }
        }
        return grid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean registerAgent(A agent, Point position) {
        if (this.grid.get(position.y).get(position.x) == null) {
            this.grid.get(position.y).set(position.x, agent);
            this.agentLocations.put(agent, position);
            return true;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point getPosition(A agent) {
        return this.agentLocations.get(agent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public A getAgentAt(Point position) {
        return this.grid.get(position.y).get(position.x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean move(A agent, Direction direction) {
        Point agentPosition = getPosition(agent);
        Point newPosition = new Point(agentPosition);
        switch (direction) {
            case UP:
                newPosition.y--;
                break;
            case DOWN:
                newPosition.y++;
                break;
            case LEFT:
                newPosition.x--;
                break;
            case RIGHT:
                newPosition.x++;
                break;
        }

        if (newPosition.x < 0 || newPosition.x >= width || newPosition.y < 0 || newPosition.y >= height) {
            // Cannot cross grid boundaries
            return false;
        }

        if (getAgentAt(newPosition) != null) {
            // Cannot move to occupied cell
            return false;
        }

        agentLocations.put(agent, newPosition);
        grid.get(agentPosition.y).set(agentPosition.x, null);
        grid.get(newPosition.y).set(newPosition.x, agent);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point getRandomFreePoint(boolean avoidEdges) {
        int x = getIntInBounds(avoidEdges ? 1 : 0, width - (avoidEdges ? 1 : 0));
        int y = getIntInBounds(avoidEdges ? 1 : 0, height - (avoidEdges ? 1 : 0));
        if (grid.get(y).get(x) == null) {
            return new Point(x, y);
        } else {
            return getRandomFreePoint(avoidEdges);
        }
    }

    /**
     * Sample a random point inside the given bounds
     * @param min Lower bound (inclusive)
     * @param max Upper bound (exclusive)
     * @return Random point between min and max
     */
    private int getIntInBounds(int min, int max) {
        return random.nextInt(max - min) + min;
    }
}
