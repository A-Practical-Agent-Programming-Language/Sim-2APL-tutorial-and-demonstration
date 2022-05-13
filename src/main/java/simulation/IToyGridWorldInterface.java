package simulation;

import environment.Direction;
import nl.uu.cs.iss.ga.sim2apl.core.deliberation.DeliberationResult;
import nl.uu.cs.iss.ga.sim2apl.core.step.EnvironmentInterface;

import java.util.List;
import java.util.concurrent.Future;

public class IToyGridWorldInterface implements EnvironmentInterface<Direction> {
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
