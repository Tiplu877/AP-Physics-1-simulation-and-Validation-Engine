package validation;

import java.util.ArrayList;
import java.util.List;

public class OscillationPeriodDetector {
    private Double previousSign = null;
    private final List<Double> turningPointTimes = new ArrayList<>();

    // Call once per simulation step with whatever signed quantity oscillates
    // (e.g. vertical velocity for a spring). Detects zero-crossings the same way
    // PeriodTracker does for circular motion, generalized to any 1D signal.
    public void sample(double signedValue, double elapsedTime) {
        if (Math.abs(signedValue) < 1e-5) return;
        double currentSign = Math.signum(signedValue);
        if (previousSign != null && currentSign != previousSign) {
            turningPointTimes.add(elapsedTime);
        }
        previousSign = currentSign;
    }

    public double getMeasuredPeriod() {
        int n = turningPointTimes.size();
        if (n < 3) return -1;
        return turningPointTimes.get(n - 1) - turningPointTimes.get(n - 3);
    }
}
