
import launcher.LauncherFrame;
import launcher.Scenario;
import launcher.SolvableProblem;
import launcher.scenarios.*;
import launcher.problems.*;

import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Scenario> scenarios = List.of(
                new RampFrictionScenario(),
                new AtwoodMachineScenario(),
                new ProjectileScenario(),
                new VerticalLoopScenario(),
                new CarOnCurveScenario(),
                new PendulumScenario(),
                new RollingRaceScenario(),
                new OrbitScenario(),
                new SpringScenario(),
                new TwoBallCollisionScenario(),
                new BalanceBeamScenario()
        );

        List<SolvableProblem> problems = List.of(
                new AngledProjectileProblem(),
                new StraightLineKinematicsProblem(),
                new FreeFallThrownUpProblem(),
                new AverageForceProblem(),
                new MinSpeedAtTopProblem(),
                new PendulumPeriodProblem(),
                new BalanceDistanceProblem()
        );


        SwingUtilities.invokeLater(() -> new LauncherFrame(scenarios, problems).setVisible(true));
    }
}
