import environment.Direction;
import environment.IToyGridWorld;
import environment.ToyGridWorld;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.awt.*;
import java.util.Random;

public class TestToyGridWorld {

    private final Random random = new Random();
    private final int width = 50;
    private final int height = 50;

    private final String theAgent = "theAgent";
    private final String otherAgent = "otherAgent";


    @DisplayName("Agent starts at registered position")
    @RepeatedTest(100)
    void testRegisterAgentIsAtPosition() {
        IToyGridWorld<String> gridWorld = createGridWorld();
        Point position = gridWorld.getRandomFreePoint(false);
        Assertions.assertTrue(gridWorld.registerAgent(theAgent, position));
        Assertions.assertEquals(position, gridWorld.getPosition(theAgent));
    }

    @DisplayName("Agent hashmap position and grid position match")
    @RepeatedTest(100)
    void testAgentPositionMatchesGridPosition() {
        IToyGridWorld<String> gridWorld = createGridWorld();
        Point position = gridWorld.getRandomFreePoint(false);
        Assertions.assertTrue(gridWorld.registerAgent(theAgent, position));
        Point agentPosition = gridWorld.getPosition(theAgent);
        Assertions.assertEquals(gridWorld.getAgentAt(agentPosition), theAgent);
    }

    @DisplayName("Agent cannot be placed on occupied square")
    @RepeatedTest(100)
    void testRejectRegisterAgentAtPosition() {
        IToyGridWorld<String> gridWorld = createGridWorld();
        Point position = gridWorld.getRandomFreePoint(false);
        Assertions.assertTrue(gridWorld.registerAgent(theAgent, position));
        Assertions.assertFalse(gridWorld.registerAgent(otherAgent, position));
        Assertions.assertEquals(gridWorld.getAgentAt(position), theAgent);
        Assertions.assertEquals(gridWorld.getPosition(theAgent), position);
    }

    @DisplayName("Test agent move action UP")
    @RepeatedTest(100)
    void testMoveUp() {
        IToyGridWorld<String> gridWorld = createGridWorld();
        Point position = gridWorld.getRandomFreePoint(true);
        Point newPosition = testMove(gridWorld, position, Direction.UP);
        Assertions.assertEquals(position.x, newPosition.x);
        Assertions.assertEquals(position.y - 1, newPosition.y);
    }

    @DisplayName("Test agent move action DOWN")
    @RepeatedTest(100)
    void testMoveDown() {
        IToyGridWorld<String> gridWorld = createGridWorld();
        Point position = gridWorld.getRandomFreePoint(true);
        Point newPosition = testMove(gridWorld, position, Direction.DOWN);
        Assertions.assertEquals(position.x, newPosition.x);
        Assertions.assertEquals(position.y + 1, newPosition.y);
    }

    @DisplayName("Test agent move action LEFT")
    @RepeatedTest(100)
    void testMoveLeft() {
        IToyGridWorld<String> gridWorld = createGridWorld();
        Point position = gridWorld.getRandomFreePoint(true);
        Point newPosition = testMove(gridWorld, position, Direction.LEFT);
        Assertions.assertEquals(position.x - 1, newPosition.x);
        Assertions.assertEquals(position.y, newPosition.y);
    }

    @DisplayName("Test agent move action RIGHT")
    @RepeatedTest(100)
    void testMoveRight() {
        IToyGridWorld<String> gridWorld = createGridWorld();
        Point position = gridWorld.getRandomFreePoint(true);
        Point newPosition = testMove(gridWorld, position, Direction.RIGHT);
        Assertions.assertEquals(position.x + 1, newPosition.x);
        Assertions.assertEquals(position.y, newPosition.y);
    }

    Point testMove(IToyGridWorld<String> gridWorld, Point position, Direction moveDirection) {
        gridWorld.registerAgent(theAgent, position);
        boolean result = gridWorld.move(theAgent, moveDirection);
        Point newPosition = gridWorld.getPosition(theAgent);
        Assertions.assertTrue(result);
        Assertions.assertNull(gridWorld.getAgentAt(position));
        Assertions.assertEquals(gridWorld.getAgentAt(newPosition), theAgent);
        return newPosition;
    }

    @DisplayName("Test agent cannot cross the upper edge")
    @RepeatedTest(100)
    void testCannotCrossUpperEdge() {
        testCannotCrossEdge(random.nextInt(width), 0, Direction.UP);
    }

    @DisplayName("Test agent cannot cross the lower edge")
    @RepeatedTest(100)
    void testCannotCrossLowerEdge() {
        testCannotCrossEdge(random.nextInt(width), height - 1, Direction.DOWN);
    }

    @DisplayName("Test agent cannot cross the left edge")
    @RepeatedTest(100)
    void testCannotCrossLeftEdge() {
        testCannotCrossEdge(0, random.nextInt(height), Direction.LEFT);
    }

    @DisplayName("Test agent cannot cross the right edge")
    @RepeatedTest(100)
    void testCannotCrossRightEdge() {
        testCannotCrossEdge(width - 1, random.nextInt(height), Direction.RIGHT);
    }

    private void testCannotCrossEdge(int positionX, int positionY, Direction moveDirection) {
        IToyGridWorld<String> gridWorld = createGridWorld();
        Point position = new Point(positionX, positionY);
        gridWorld.registerAgent(theAgent, position);
        Assertions.assertFalse(gridWorld.move(theAgent, moveDirection));
        Assertions.assertEquals(gridWorld.getPosition(theAgent), position);
        Assertions.assertEquals(gridWorld.getAgentAt(position), theAgent);
    }

    @DisplayName("Test agent cannot move to occupied square")
    @RepeatedTest(100)
    void testCannotMoveToOccupiedSquare() {
        cannotMoveToOccupiedSquare(Direction.UP);
        cannotMoveToOccupiedSquare(Direction.DOWN);
        cannotMoveToOccupiedSquare(Direction.LEFT);
        cannotMoveToOccupiedSquare(Direction.RIGHT);
    }

    private void cannotMoveToOccupiedSquare(Direction moveDirection) {
        IToyGridWorld<String> gridWorld = createGridWorld();
        Point position = gridWorld.getRandomFreePoint(true);
        Point position2;
        switch (moveDirection) {
            case UP:
                position2 = new Point(position.x, position.y - 1);
                break;
            case DOWN:
                position2 = new Point(position.x, position.y + 1);
                break;
            case LEFT:
                position2 = new Point(position.x - 1, position.y);
                break;
            case RIGHT:
                position2 = new Point(position.x + 1, position.y);
                break;
            default:
                position2 = position;
                break;
        }
        Assertions.assertTrue(gridWorld.registerAgent(theAgent, position));
        Assertions.assertTrue(gridWorld.registerAgent(otherAgent, position2));
        Assertions.assertFalse(gridWorld.move(theAgent, moveDirection));
        Assertions.assertEquals(gridWorld.getPosition(theAgent), position);
        Assertions.assertEquals(gridWorld.getAgentAt(position), theAgent);
        Assertions.assertEquals(gridWorld.getPosition(otherAgent), position2);
        Assertions.assertEquals(gridWorld.getAgentAt(position2), otherAgent);
    }

    private IToyGridWorld<String> createGridWorld() {
        return new ToyGridWorld<>(new Random(), this.width, this.height);
    }
}
