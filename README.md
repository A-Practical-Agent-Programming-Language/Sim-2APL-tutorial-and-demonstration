# A Sim-2APL Simulation Tutorial
In this tutorial, we will create a very simple demonstration of a Sim-2APL simulation.

For instructions on setting up this repository and skipping this tutorial, 
refer to [INSTALLATION.md](INSTALLATION.md).

Sim-2APL is a library for programming belief-desire-intention (BDI) agents for use in time-step synchronized
simulations. Such a simulation progresses in discrete time steps, where first, all agents go through 
a _deliberation cycle_ in which they determine what actions to take, after which all actions are materialized
in the environment.

The Sim-2APL library is available on Github. It can be added to a Java project by adding the 
[JAR file](https://github.com/A-Practical-Agent-Programming-Language/Sim-2APL/releases/tag/v2.0.0)
as a dependency, or through Maven as follows.

First, clone the Sim-2APL repository, and install it to the local .m2 repository:

```bash
git clone https://github.com/A-Practical-Agent-Programming-Language/Sim-2APL.git sim2apl
cd sim2apl
mvn install
```

Next, for this tutorial, create a new Maven project, and add Sim-2APL to the POM file as a dependency

```xml
<dependencies>
    <dependency>
        <groupId>nl.uu.iss.ga</groupId>
        <artifactId>sim2apl-matrix</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### Environment
The environment for this tutorial is a very simple grid world, in which the agents can move around
and nothing else.

The agents can move by specifying a [direction](src/main/java/environment/Direction.java) 
(`up`, `down`, `left` or `right`)

The environment can be found in [ToyGridWorld](src/main/java/environment/ToyGridWorld.java).

### The Tutorial's Goal
In this simulation, each agent will move through the grid world independently from all other agents.
Each agent will pick a random spot in the environment, and adopt a goal to move to that environment.
We will implement a plan to move to the point specified in the goal, and create rules for when to select that plan.

## The Agent
We define an agent in terms of its _beliefs_, specified as `Context`s, its goals, its plans, and its plan schemes.

Because we already know all agents' actions will be a direction,
we use the [`Direction`](src/main/java/environment/Direction.java)
class as a generic argument of all Sim-2APL constructs in the following.

### Beliefs
Let us start by specifying an agent's beliefs. This is done by implementing a Sim-2APL context, which is
an empty interface, and can act as a model for all information relevant to the agents.

On this context, we will maintain the agent's subjective beliefs about its position and the environment
size. We additionally allow it to store a Random object, so stochastic decision making can be seeded
for repeated execution.

```java
public class AgentBeliefContext implements Context {
    private final Random random;
    private final String name;
    private Point position;
    private final int environmentWidth;
    private final int environmentHeight;

    public AgentBeliefContext(
            Random random, 
            Point position, 
            int environmentWidth, 
            int environmentHeight
    ) {
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
```

All context an agent has can later be accessed from goals, plans and plan schemes through either the
`AgentContextInterface` or the `PlanToAgentInterface` through their class name as follows:

```java
AgentBeliefContext context = planToAgentInterface.getContext(AgentBeliefContext.class)
```

### Goals
Next, let's create two goals. The first goal, 
[`MoveTowardsGoal`](src/main/java/simulation/agent/MoveTowardsGoal.java) can specify a location to move towards 
(encoded as a Java AWT `Point` for simplicity). This goal is achieved when the agent beliefs it is 
at the position specified by the goal.


```java
public class MoveTowardsGoal extends Goal<Direction> {

    private final Point destination;

    public MoveTowardsGoal(Point destination) {
        this.destination = destination;
    }

    public Point getDestination() {
        return destination;
    }

    @Override
    public boolean isAchieved(AgentContextInterface<Direction> agentContextInterface) {
        AgentBeliefContext context = 
                agentContextInterface.getContext(AgentBeliefContext.class);
        return this.destination.equals(context.getPosition());
    }
}
```

The second goal,
[`KeepMovingGoal`](src/main/java/simulation/agent/KeepMovingGoal.java),
is used to make sure the agent always has `MoveTowardsGoal`. 
We intend to use this goal to make sure the agent adopts a new `MoveTowardsGoal` once the previous
`MoveTowardsGoal` is achieved.

As such, we do not have to store any other information on this object, and we know it should never
be achieved:

```java
public class KeepMovingGoal extends Goal<Direction> {

    @Override
    public boolean isAchieved(AgentContextInterface<Direction> agentContextInterface) {
        return false;
    }
}
```

### Plans
With that in place, we have to specify how an agent will achieve all possible goals they can have, which
we do by implementing a _plan_.
Typically, the way an agent achieves a goal may depend on context, so multiple plans may exist for the same goal,
that take different approaches. An agent selects the best plan under the circumstances in the _plan schemes_, which
we will deal with next.

For this demonstration, the agents' behavior is not complex, so we only need one plan for each goal.

Let's first implement a plan that should achieve the `KeepMovingGoal`, i.e., that finds a new point in the
grid world, and adopts a `MoveTowardsGoal` to move to that point.

Note that we only want the agent to pursue one destination at the time, so we should only adopt
a new `MoveTowardsGoal` if the agent is not currently pursing another `MoveTowardsGoal`.

This plan will not have to return any actions yet, as it only concerns a goal update, which is
an internal action that does not affect the environment.

```java
public class KeepMovingPlan extends Plan<Direction> {

    @Override
    public Direction execute(PlanToAgentInterface<Direction> planToAgentInterface) throws PlanExecutionError {
        
        // Check no other MoveTowardsGoal is currently being pursued
        if (!planToAgentInterface.hasGoal(MoveTowardsGoal.class)) {
            
            // Pick a new destination
            Point newDestination = sampleNewPoint(planToAgentInterface);
            
            // Create a goal to move to that destination
            MoveTowardsGoal newGoal = new MoveTowardsGoal(newDestination);

            // Adopt the new goal, so the agent starts pursuing it
            planToAgentInterface.adoptGoal(newGoal);
        }

        // The current plan only performs internal actions. No need to return any actions to the environment
        return null;
    }

    /**
     * Lets the agent determine a new random destination within the environment grid
     * @param planToAgentInterface planToAgentInterface
     * @return Random point in the grid environment
     */
    private Point sampleNewPoint(PlanToAgentInterface<Direction> planToAgentInterface) {
        AgentBeliefContext context = planToAgentInterface.getContext(AgentBeliefContext.class);
        int newDestinationX = context.getRandom().nextInt(context.getEnvironmentWidth());
        int newDestinationY = context.getRandom().nextInt(context.getEnvironmentHeight());
        return new Point(newDestinationX, newDestinationY);
    }
}
```

Similarly, for the `MoveTowardsGoal`, we need a plan that lets the agent move through the environment 
towards the intended position.
We will assume each agent can make one move in the environment per time step, so the plan should result
in a single action, encoding a direction.

We use a simple heuristic, first aligning our X-coordinate in the environment, and then
moving in towards the Y-coordinate. If nothing is blocking our way, that should get us to our goal.

Note that the agent maintains its own subjective belief about its position, so we need to
update our beliefs based on the direction that we want to move to:

```java
public class MoveTowardsPlan extends RunOncePlan<Direction> {

    private final MoveTowardsGoal goal;

    public MoveTowardsPlan(MoveTowardsGoal goal) {
        this.goal = goal;
    }

    @Override
    public Direction executeOnce(PlanToAgentInterface<Direction> planToAgentInterface) throws PlanExecutionError {
        AgentBeliefContext context = planToAgentInterface.getContext(AgentBeliefContext.class);
        Point currentPosition = new Point(context.getPosition());

        Direction move = null;

        if (currentPosition.x == goal.getDestination().x) {
            if (currentPosition.y < goal.getDestination().y) {
                currentPosition.y++;
                move = Direction.DOWN;
            } else if (currentPosition.y > goal.getDestination().y) {
                currentPosition.y--;
                move = Direction.UP;
            }
        } else if (currentPosition.x < goal.getDestination().x) {
            currentPosition.x++;
            move = Direction.RIGHT;
        } else {
            currentPosition.x--;
            move = Direction.LEFT;
        }

        // Update our belief about our current position based on the move we expect to make
        context.setPosition(currentPosition);

        // This is collected by Sim-2APL, which sends it to the environment after all agents have
        // decided their move
        return move;
    }
}
```

### Plan Schemes
The last thing we need, is a set of rules for which plans to use to achieve what goals, under what circumstances.
For this tutorial, the behavior is very simple: Each goal has one plan that can achieve it, so we just make sure
that we return exactly that plan:

```java
public class GoalPlanScheme implements PlanScheme<Direction> {

    @Override
    public Plan<Direction> instantiate(Trigger trigger, AgentContextInterface<Direction> agentContextInterface) {

        if (trigger instanceof MoveTowardsGoal) {
            return new MoveTowardsPlan((MoveTowardsGoal) trigger);
        } else if (trigger instanceof KeepMovingGoal) {
            return new KeepMovingPlan();
        }

        return null;
    }
}
```

### Creating the agent
With all the building blocks of the agent in place, we can easily create any number of agents that we want, by first instantiating
the Sim-2APL Platform:

```java
Platform platform = Platform.newPlatform(
        4, // Use 4 threads for concurrent execution of agents 
        new DefaultMessenger<Direction>() // Required for platform creation, but not used here
);
```

The relevant building blocks of an agent are created through the `AgentArguments`, a builder for Sim-2APL agents:

```java
AgentArguments<Direction> arguments = new AgentArguments<>();
arguments.addGoalPlanScheme(new GoalPlanScheme()); // Add the plan scheme for goal planning rules

Point initialPosition = new Point(0,0);
AgentBeliefContext beliefContext = new AgentBeliefContext(
    new Random(seed),
    initialPosition,
    10, // environment width
    10 // Environment height
);

arguments.addContext(beliefContext);
Agent<Direction> agent = new Agent<>(platform, arguments);

// We just adopt this goal, and from now on, the agent will pick random destinations to move towards!
agent.adoptGoal(new KeepMovingGoal());
```

## Creating the Environment Interface
We now have a simulation in which agents can determine an action, and maintain updated beliefs about their 
position in the environment. However, we still need to make sure those moves are actually performed in the environment.

Sim-2APL provides an `EnvironmentInterface` to make that connection that acts as a simulation event listener. 
This allows a wide variety of existing environments (that may already contain complex environmental dynamics) and
simulation platforms to be connected. The environment does not even have to run in Java, if a common communication
channel can be established between it and Java.

Every class implement the `EnvironmentInterface` and registered with the simulation engine is notified of two phases
during _each_ time step, _step starting_ and _step finished_, and of the simulation having finished.

```java
public class ToyGridWorldInterface implements EnvironmentInterface<Direction> {
    @Override
    public void stepStarting(long l) {

    }

    @Override
    public void stepFinished(long l, int i, List<Future<DeliberationResult<Direction>>> list) {

    }

    @Override
    public void simulationFinished(long l, int i) {

    }
}
```

In the step finished method of any time step _t_, all agent actions are passed (maintaining the order in which the agents that produced
them were scheduled). In the implementation of this method, the actions should be sent to the environment, that is
then requested to calculate the state _t_+1. In the current demonstration, we will do that by iterating over all actions, 
and calling the `move` method in the Grid World environment. Let's make sure we have access to that environment in
our implementation of the environment interface, and that we can add agents.

```java
import environment.IToyGridWorld;
import nl.uu.cs.iss.ga.sim2apl.core.agent.AgentID;

public class ToyGridWorldInterface implements EnvironmentInterface<Direction> {

    private final IToyGridWorld<AgentID> toyGridWorld;

    public ToyGridWorldInterface(Random random, int width, int height) {
        this.toyGridWorld = new ToyGridWorld<>(random, width, height);
    }

    public void stepStarting(long l) {

    }

    @Override
    public void stepFinished(long l, int i, List<Future<DeliberationResult<Direction>>> list) {
        for(Future<DeliberationResult<Direction>> deliberationResultFuture : agentActions) {
            try {
                // The deliberation result is a tuple containing the agent ID
                // and an (ordered) list of actions produced by the corresponding agents in the last time step
                DeliberationResult<Direction> deliberationResult = deliberationResultFuture.get();
                
                // Iterate over all actions and materialize them in the environment
                for(Direction direction : deliberationResult.getActions()) {
                    toyGridWorld.move(deliberationResult.getAgentID(), direction);
                }

            } catch (InterruptedException | ExecutionException | URISyntaxException e) {
                // All futures should have finished, so an InterruptedException should not occur
                // However, the ExecutionException is thrown here, if there is an error in the agent code
                e.printStackTrace();
            }
        }
    }

    @Override
    public void simulationFinished(long l, int i) {

    }

    public void registerAgent(Agent<Direction> agent, Point initialPosition) {
        this.getToyGridWorld().registerAgent(agent.getAID(), initialPosition);
    }
}
```

For this demonstration, we do not need to implement the `stepStarting` and `simulationFinished` methods.
However, more complex simulations may need these methods.

For the first, consider for example an environment programmed in Python or C, C++ or C#, with an additional communication
layer. In the `stepFinished` method, we would call on the environment (using the communication protocol) to materialize
all the agent actions. This will result in the next state _t_+1 of the environment being calculated, but now, before
we start the next step, we want to request all information relevant to the agents of state _t_+1 from the environment,
e.g., to allow agents to update their beliefs. That would then happen in the `stepStarting` method.

The `simulationFinished` method is there for any clean-up operations before the java process ends. E.g., file handlers,
or communication with an external environment.

## Creating the Simulation Platform
With everything in place, we can now start a Sim-2APL simulation with the Grid World environment by creating
the platform, instantiating the environment interface, creating a simulation engine and registering the environment
interface with it and then creating some agents.

We have already seen how most of that is done previously, so we just have to combine that results.

```java
import java.net.URISyntaxException;

class Main {

    public static void main(String[] args) {
        // We let the platform run with 4 threads, allowing concurrent execution
        platform = Platform.newPlatform(4, new DefaultMessenger<Direction>());
        
        // Remove this seed for stochastic simulations
        Random random = new Random(42);

        // This creates the GridWorld environment instance, and handles communication with it based on
        // the time step events
        environmentInterface = new ToyGridWorldInterface(
                new Random(random.nextLong()),
                ns.getInt(Constants.ARG_WIDTH),
                ns.getInt(Constants.ARG_HEIGHT)
        );
        
        // We can pass this (and any other) environment interface implementation to the constructor of the
        // simulation engine to automatically register it as a subscriber
        // The simulation engine is what makes sure all the steps are run
        simulationEngine = new DefaultSimulationEngine<>(platform, environmentInterface);

        // Let's try with 4 agents
        tryCreateAgents(4, random);

        // We start the simulation once all agents are ready
        simulationEngine.start();
    }

    public static void tryCreateAgent(int nAgents, Random random) {
        int i = 0;
        while (i < nAgents) {
            try {
                // Note how we are seeding the random object of each agent with a random number from our existing
                // random object. If that initial object is seeded, each agent will have access to their own
                // random object, so execution order of the agents does not change the random numbers they draw
                createAgent(Integer.toString(i), random.nextInt());
                i++;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    // This method corresponds to agent instantiation code shown at the end of the `The Agent` section
    public void createAgent(String seed) throws URISyntaxException {
        AgentArguments<Direction> arguments = new AgentArguments<>();
        arguments.addGoalPlanScheme(new GoalPlanScheme()); // Add the plan scheme for goal planning rules

        Point initialPosition = new Point(0, 0);
        AgentBeliefContext beliefContext = new AgentBeliefContext(
                new Random(seed),
                initialPosition,
                10, // environment width
                10 // Environment height
        );

        arguments.addContext(beliefContext);
        Agent<Direction> agent = new Agent<>(platform, arguments);

        // We just adopt this goal, and from now on, the agent will pick random destinations to move towards!
        agent.adoptGoal(new KeepMovingGoal());
    }
}
```

And now the simulation is ready to run!

### Wrapping up
This repository contains the sample simulation with some additional features that can be explored by the reader.

For example, the Grid World is printed to the environment after each time step, by calling the
`draw` method of the [`EnvironmentConsoleView`](src/main/java/environment/EnvironmentConsoleView.java) from the
`stepFinished` method in the [`ToyGridWorldInterface'](src/main/java/simulation/ToyGridWorldInterface.java).

Additionally, because the standard AgentID is a relatively long URI, we assign each agent a simple string representation
that is used for displaying it in the environment.

Lastly, a move in the environment can fail if it takes the agent of the grid, or the target cell is already occupied
by another agent.
The `stepFinished` method in the [`ToyGridWorldInterface`](src/main/java/simulation/ToyGridWorldInterface.java)
tracks all failed `move` actions, and notifies the agents through an 
[external trigger](src/main/java/simulation/agent/MoveFailedTrigger.java),
that allows the agent to update their beliefs about their current position.
To this end, another plan scheme, 
called the [`ExternalTriggerPlanScheme`](src/main/java/simulation/agent/ExternalTriggerPlanScheme.java)
needs to be added to the agent by calling 
```java
arguments.addExternalTriggerPlanScheme(new ExternalTriggerPlanScheme());
```

External triggers are always processed before goals, making sure that the agents beliefs are up-to-date before they
start deliberating their next move.

Of course, the reasoning of agents is limited, as this tutorial is aimed at demonstrating how to create
Sim-2APL agents with a simulation environment. Inspired readers are encouraged to try to extend the simulation with 
additional reasoning and features.