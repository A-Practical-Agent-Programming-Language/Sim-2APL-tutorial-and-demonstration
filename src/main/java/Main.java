import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import simulation.Simulation;
import util.Constants;

public class Main {

    public static void main(String[] args) {
        ArgumentParser parser = createParser();
        try {
            Namespace ns = parser.parseArgs(args);
            new Simulation(ns);
        } catch (ArgumentParserException e) {
            System.exit(0);
        }
    }

    private static ArgumentParser createParser() {
        ArgumentParser parser = ArgumentParsers.newFor("Sim-2APL Demonstration Simulation").build()
                .defaultHelp(true)
                .description("This is a simple demonstration of how to use Sim-2APL with a grid world" +
                        "simulation environment.\n" +
                        "The agents move along a 2D grid to random destinations.\n" +
                        "Sim-2APL synchronizes the agents, so all agents move at the same time, unless " +
                        "they have already reached their destination, in which case they first determine " +
                        "a new destination by sampling a random point on the grid ");
        parser.addArgument("-x", "--" + Constants.ARG_WIDTH)
                .type(Integer.class)
                .help("The number of horizontal cells in the grid")
                .setDefault(10);

        parser.addArgument("-y", "--" + Constants.ARG_HEIGHT)
                .type(Integer.class)
                .help("The number of vertical cells in the grid")
                .setDefault(10);

        parser.addArgument("-a", "--" + Constants.ARG_N_AGENTS)
                .type(Integer.class)
                .help("The number of agents to initiate the simulation with")
                .setDefault(4);

        parser.addArgument("-d", "--" + Constants.ARG_BETWEEN_STEP_DELAY)
                .type(Integer.class)
                .help("The number of milliseconds to delay between each time step (to allow interpreting visualization")
                .setDefault(1000);

        parser.addArgument("-s", "--" + Constants.ARG_SEED)
                .type(Integer.class)
                .help("The seed to use for repeatable simulations. If left empty, simulation will progress " +
                        "stochastically.");

        return parser;
    }

}
