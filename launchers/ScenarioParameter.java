package launcher;

public class ScenarioParameter {
    public String label;
    public String key;
    public double min;
    public double max;
    public double defaultValue;
    public double step;

    public ScenarioParameter(String label, String key, double min, double max, double defaultValue, double step) {
        this.label = label;
        this.key = key;
        this.min = min;
        this.max = max;
        this.defaultValue = defaultValue;
        this.step = step;
    }
}