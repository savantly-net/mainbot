package net.savantly.mainbot.dom.promptTemplate;

import java.util.List;
import java.util.Map;

public class PromptParser {

    public String parse(String template, Map<String, Object> values) {
        String parsed = template;
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            parsed = parsed.replace("{{" + entry.getKey() + "}}", convertToString(entry.getValue()));
        }
        return parsed;
    }

    private String convertToString(Object value) {
        if (value instanceof String) {
            return (String) value;
        }

        if (List.class.isAssignableFrom(value.getClass())) {
            var list = (List<?>) value;
            return list.stream().map(this::convertToString).reduce("", (a, b) -> a + "\n" + b);
        }
        return value.toString();
    }

}
