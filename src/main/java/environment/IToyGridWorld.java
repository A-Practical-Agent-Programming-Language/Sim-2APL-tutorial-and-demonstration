package environment;

import java.awt.*;

public interface IToyGridWorld<A> {

    /**
     * Place a new agent in the environment
     *
     * @param agent     Agent identifier
     * @param position  Agent position
     * @return          True iff agent could be placed
     */
    boolean registerAgent(A agent, Point position);

    /**
     * @return the width of the grid world
     */
    int getWidth();

    /**
     * @return the height of the grid world
     */
    int getHeight();

    /**
     * THe position of the agent, if it is located in the environment
     * @param agent Identifier of agent to locate
     * @return Point representing position in the grid world, with (0,0) the top left cell
     */
    Point getPosition(A agent);

    /**
     * @param position A point in the grid world, with (0,0) the top left cell
     * @return The identifier of the agent occupying the position, or null if the position is empty
     */
    A getAgentAt(Point position);

    /**
     * Move an agent one step in the specified direction.
     * This action can fail if a) the move places the agent outside the grid or b) the move places the agent in
     * a cell that is already occupied
     *
     * @param agent     Identifier of the agent to move
     * @param direction The direction in which to move the agent
     * @return          True iff the agent could be moved
     */
    boolean move(A agent, Direction direction);

    /**
     * Find a random point in the grid that is not yet occupied
     *
     * @param avoidEdges If true, the returned point will not be on the border of the grid
     * @return Random point within the grid boundaries that is not yet occupied
     */
    Point getRandomFreePoint(boolean avoidEdges);
}
