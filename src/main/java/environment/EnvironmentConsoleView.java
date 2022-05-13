package environment;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.awt.*;
import java.util.List;
import java.util.Map;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * An implementation of the IEnvironmentView that displays the grid and its agent
 * in a console window
 *
 * @param <A> The generic type of the agent representation in the ToyGridWorld
 */
public class EnvironmentConsoleView<A> implements IEnvironmentView<A> {

    private final IToyGridWorld<A> toyGridWorld;

    public EnvironmentConsoleView(IToyGridWorld<A> toyGridWorld) {
        this.toyGridWorld = toyGridWorld;
        AnsiConsole.systemInstall();
        System.setProperty("org.jline.terminal.dumb", "true");
    }

    @Override
    public void draw(long timeStep, Map<Point, List<A>> targets) {
        System.out.println(ansi().eraseScreen());
        drawLine();
        for(int r = 0; r < toyGridWorld.getHeight(); r++) {
            System.out.print("| ");
            for(int c = 0; c < toyGridWorld.getWidth(); c++) {
                Point cell = new Point(c, r);
                A agent = toyGridWorld.getAgentAt(cell);
                if (agent != null) {
                    System.out.print(ansi().fg(Ansi.Color.GREEN).a(agent).a(" ").reset());
                } else if (targets.containsKey(cell) && !targets.get(cell).isEmpty()) {
                    agent = targets.get(cell).get(0);
                    System.out.print(ansi().fg(Ansi.Color.RED).a("d").a(agent).reset());
                } else {
                    System.out.print("  ");
                }
                System.out.print("| ");
            }
            drawLine();
        }
        System.out.println("\n");
        System.out.println("Legend:");
        System.out.println(ansi().fg(Ansi.Color.GREEN).a("x").reset().a(": Position of agent x"));
        System.out.println(ansi().fg(Ansi.Color.RED).a("dx").reset().a(": Destination of agent x"));
        System.out.println("\n\n");
    }

    /**
     * Draws a horizontal line to separate the rows
     */
    private void drawLine() {
        System.out.print("\n__");
        for(int i = 0; i < toyGridWorld.getWidth(); i++) {
            System.out.print("____");
        }
        System.out.println("");
    }

}
