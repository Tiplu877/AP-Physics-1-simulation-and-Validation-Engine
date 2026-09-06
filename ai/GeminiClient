package ai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class GeminiClient {

    // Get a free key at https://aistudio.google.com/apikey -- no credit card needed.
    // Reads from an environment variable so the key never has to be hardcoded/committed.
    // To set it: on Mac/Linux, `export GEMINI_API_KEY=your_key_here` before running;
    // on Windows, set it as a system environment variable, or run your IDE with it configured.
    private static final String API_KEY = System.getenv("GEMINI_API_KEY");
    private static final String MODEL = "gemini-3.5-flash";
    private static final String ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/" + MODEL + ":generateContent";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String generate(String prompt) throws Exception {
        if (API_KEY == null || API_KEY.isBlank()) {
            throw new IllegalStateException(
                    "GEMINI_API_KEY environment variable is not set. Get a free key at " +
                            "https://aistudio.google.com/apikey and set it before running.");
        }
        System.out.println("Using key starting with: " + API_KEY.substring(0, Math.min(8, API_KEY.length())));

        String requestBody = "{\"contents\":[{\"parts\":[{\"text\":" + escapeJsonString(prompt) + "}]}]}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        int maxRetries = 3;
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return extractTextFromResponse(response.body());
            }

            // 503 = model temporarily overloaded on Google's end, worth a short retry.
            // Other error codes (401 bad key, 400 bad request, 429 rate limit) won't be
            // fixed by retrying, so fail immediately instead of wasting attempts.
            if (response.statusCode() != 503 || attempt == maxRetries - 1) {
                throw new RuntimeException("Gemini API returned status " + response.statusCode() + ": " + response.body());
            }

            Thread.sleep(1000L * (attempt + 1)); // 1s, then 2s, then 3s
        }

        throw new RuntimeException("Gemini API still unavailable after " + maxRetries + " attempts");
    }

    @SuppressWarnings("unchecked")
    private String extractTextFromResponse(String responseBody) {
        Map<String, Object> parsed = SimpleJson.parseObject(responseBody);
        List<Object> candidates = (List<Object>) parsed.get("candidates");
        Map<String, Object> firstCandidate = (Map<String, Object>) candidates.get(0);
        Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
        List<Object> parts = (List<Object>) content.get("parts");
        Map<String, Object> firstPart = (Map<String, Object>) parts.get(0);
        return (String) firstPart.get("text");
    }

    private String escapeJsonString(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\t': sb.append("\\t"); break;
                default: sb.append(c);
            }
        }
        sb.append("\"");
        return sb.toString();
    }
}
