package ai;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// A minimal JSON parser -- handles exactly what this project needs (objects, arrays,
// strings, numbers, booleans), not general-purpose JSON edge cases. Kept dependency-free
// on purpose, matching the rest of this project (no Maven/Gradle, just javac). If your
// JSON needs grow beyond this, a real library (e.g. org.json, Gson) would be worth
// adding as a proper dependency instead of extending this by hand.
public class SimpleJson {

    private final String text;
    private int pos = 0;

    private SimpleJson(String text) {
        this.text = text;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseObject(String jsonText) {
        SimpleJson parser = new SimpleJson(jsonText);
        parser.skipWhitespace();
        Object result = parser.parseValue();
        if (!(result instanceof Map)) {
            throw new IllegalArgumentException("Expected a JSON object at the top level");
        }
        return (Map<String, Object>) result;
    }

    private Object parseValue() {
        skipWhitespace();
        char c = text.charAt(pos);
        if (c == '{') return parseObjectInternal();
        if (c == '[') return parseArray();
        if (c == '"') return parseString();
        if (c == 't' || c == 'f') return parseBoolean();
        if (c == 'n') return parseNull();
        return parseNumber();
    }
    private Object parseNull() {
        if (text.startsWith("null", pos)) { pos += 4; return null; }
        throw new IllegalArgumentException("Invalid literal at position " + pos);
    }
    private Map<String, Object> parseObjectInternal() {
        Map<String, Object> map = new LinkedHashMap<>();
        expect('{');
        skipWhitespace();
        if (peek() == '}') { pos++; return map; }

        while (true) {
            skipWhitespace();
            String key = parseString();
            skipWhitespace();
            expect(':');
            Object value = parseValue();
            map.put(key, value);
            skipWhitespace();
            char next = text.charAt(pos++);
            if (next == '}') break;
            if (next != ',') throw new IllegalArgumentException("Expected ',' or '}' at position " + pos);
        }
        return map;
    }

    private List<Object> parseArray() {
        List<Object> list = new ArrayList<>();
        expect('[');
        skipWhitespace();
        if (peek() == ']') { pos++; return list; }

        while (true) {
            list.add(parseValue());
            skipWhitespace();
            char next = text.charAt(pos++);
            if (next == ']') break;
            if (next != ',') throw new IllegalArgumentException("Expected ',' or ']' at position " + pos);
        }
        return list;
    }

    private String parseString() {
        expect('"');
        StringBuilder sb = new StringBuilder();
        while (text.charAt(pos) != '"') {
            char c = text.charAt(pos);
            if (c == '\\') {
                pos++;
                char escaped = text.charAt(pos);
                switch (escaped) {
                    case 'n': sb.append('\n'); break;
                    case 't': sb.append('\t'); break;
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    default: sb.append(escaped);
                }
            } else {
                sb.append(c);
            }
            pos++;
        }
        pos++; // closing quote
        return sb.toString();
    }

    private Double parseNumber() {
        int start = pos;
        while (pos < text.length() && "-+.eE0123456789".indexOf(text.charAt(pos)) >= 0) pos++;
        return Double.parseDouble(text.substring(start, pos));
    }

    private Boolean parseBoolean() {
        if (text.startsWith("true", pos)) { pos += 4; return true; }
        if (text.startsWith("false", pos)) { pos += 5; return false; }
        throw new IllegalArgumentException("Invalid boolean at position " + pos);
    }

    private void expect(char c) {
        skipWhitespace();
        if (text.charAt(pos) != c) {
            throw new IllegalArgumentException("Expected '" + c + "' at position " + pos + " but found '" + text.charAt(pos) + "'");
        }
        pos++;
    }

    private char peek() {
        skipWhitespace();
        return text.charAt(pos);
    }

    private void skipWhitespace() {
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) pos++;
    }

    // Extracts the first top-level {...} block from a string -- useful because LLM
    // responses often wrap JSON in prose or markdown code fences despite instructions
    // not to. Finds the first '{' and its matching closing '}', ignoring braces inside
    // string literals so it doesn't get confused by braces appearing in text values.
    public static String extractJsonObject(String rawText) {
        int start = rawText.indexOf('{');
        if (start < 0) throw new IllegalArgumentException("No JSON object found in response: " + rawText);

        int depth = 0;
        boolean inString = false;
        boolean escaped = false;
        for (int i = start; i < rawText.length(); i++) {
            char c = rawText.charAt(i);
            if (escaped) { escaped = false; continue; }
            if (c == '\\') { escaped = true; continue; }
            if (c == '"') { inString = !inString; continue; }
            if (inString) continue;
            if (c == '{') depth++;
            if (c == '}') {
                depth--;
                if (depth == 0) return rawText.substring(start, i + 1);
            }
        }
        throw new IllegalArgumentException("Unterminated JSON object in response: " + rawText);
    }
}
