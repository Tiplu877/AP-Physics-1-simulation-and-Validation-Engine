package validation;

public class ValidationOutcome {
    public String testName;
    public String description;
    public double theoreticalValue;
    public double measuredValue;
    public String unit;
    public boolean passed; // for pass/fail-style tests instead of numeric comparison
    public String notes;

    public double percentError() {
        if (theoreticalValue == 0) return measuredValue == 0 ? 0 : Double.POSITIVE_INFINITY;
        return Math.abs((measuredValue - theoreticalValue) / theoreticalValue) * 100.0;
    }
}