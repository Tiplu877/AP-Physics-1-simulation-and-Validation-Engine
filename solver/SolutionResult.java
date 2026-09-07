package solver;

import java.util.ArrayList;
import java.util.List;

public class SolutionResult {
    public List<SolutionStep> steps = new ArrayList<>();
    public double finalAnswer;
    public String finalUnit;

    public void addStep(String description, String formula, String substitution, double result, String unit) {
        steps.add(new SolutionStep(description, formula, substitution, result, unit));
    }
}