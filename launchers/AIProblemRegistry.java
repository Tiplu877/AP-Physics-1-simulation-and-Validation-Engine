package launcher;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AIProblemRegistry {
    private final Map<String, AIProblemHandler> handlers = new LinkedHashMap<>();

    public void register(AIProblemHandler handler) {
        handlers.put(handler.getProblemTypeKey(), handler);
    }

    public AIProblemHandler get(String problemTypeKey) {
        return handlers.get(problemTypeKey);
    }

    public String buildSolvePrompt(String studentProblemText) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a physics tutor solving an AP Physics 1 problem step by step. ");
        sb.append("Respond with ONLY a JSON object, no other text, no markdown formatting, in exactly this shape:\n");
        sb.append("{\n");
        sb.append("  \"steps\": [\n");
        sb.append("    {\"description\": \"what this step finds and why\", \"formula\": \"the equation in symbols\", ");
        sb.append("\"substitution\": \"the equation with numbers plugged in\", \"result\": <number>, \"unit\": \"<unit>\"}\n");
        sb.append("  ],\n");
        sb.append("  \"finalAnswer\": <number>,\n");
        sb.append("  \"finalUnit\": \"<unit>\",\n");
        sb.append("  \"problemType\": \"<see below, or null>\",\n");
        sb.append("  \"knowns\": {\"<key>\": <number>, ...}\n");
        sb.append("}\n\n");
        sb.append("Solve the problem correctly and completely, with as many steps as needed -- don't skip algebra. ");
        sb.append("Every step's \"result\" must be a plain number (no units in the number itself).\n\n");
        sb.append("Additionally, ONLY IF this problem matches one of these specific types AND you can identify ");
        sb.append("all its required values, include \"problemType\" and \"knowns\" so a simulation can be shown. ");
        sb.append("Otherwise set \"problemType\" to null and omit \"knowns\" (or leave it empty) -- most problems won't match, and that's fine.\n");

        for (AIProblemHandler handler : handlers.values()) {
            sb.append("- \"").append(handler.getProblemTypeKey()).append("\": ")
                    .append(handler.getDescription())
                    .append(" Required keys: ").append(String.join(", ", handler.getRequiredKeys()))
                    .append("\n");
        }

        sb.append("\nStudent's problem:\n").append(studentProblemText);
        return sb.toString();
    }
    // Builds the instructions given to the AI, listing every known problem type and
    // exactly what values to extract for each -- this IS the AI's vocabulary; it can
    // only classify into one of these types, nothing else.
    public String buildClassificationPrompt(String studentProblemText) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are classifying a physics word problem into exactly one of the following types, ");
        sb.append("and extracting the known numeric values. Respond with ONLY a JSON object, no other text, ");
        sb.append("no markdown formatting, in exactly this shape:\n");
        sb.append("{\"problemType\": \"<one of the keys below>\", \"knowns\": {\"<key>\": <number>, ...}}\n\n");
        sb.append("Available problem types:\n");

        for (AIProblemHandler handler : handlers.values()) {
            sb.append("- \"").append(handler.getProblemTypeKey()).append("\": ")
                    .append(handler.getDescription())
                    .append(" Required keys: ").append(String.join(", ", handler.getRequiredKeys()))
                    .append("\n");
        }

        sb.append("\nIf gravity isn't mentioned, don't include \"g\" in knowns (a default of 9.81 will be used).\n");
        sb.append("If the problem doesn't clearly match any type above, respond with ");
        sb.append("{\"problemType\": \"unknown\", \"knowns\": {}}\n\n");
        sb.append("Student's problem:\n").append(studentProblemText);

        return sb.toString();
    }

    public List<AIProblemHandler> getAll() {
        return List.copyOf(handlers.values());
    }
}