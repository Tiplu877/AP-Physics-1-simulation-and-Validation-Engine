package solver;

public class SolutionStep {
    public String description;   // e.g. "Find the vertical velocity component"
    public String formula;       // e.g. "vy = v * sin(angle)"
    public String substitution;  // e.g. "vy = 25 * sin(40°)"
    public double result;
    public String unit;          // e.g. "m/s"

    public SolutionStep(String description, String formula, String substitution, double result, String unit) {
        this.description = description;
        this.formula = formula;
        this.substitution = substitution;
        this.result = result;
        this.unit = unit;
    }
}