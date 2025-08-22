package daluai.stml.stml_render.simple_text;

import java.util.Arrays;

public enum Alignment {

    HORIZONTAL, VERTICAL;

    public static Alignment parse(String string) {
        if (string != null && string.isEmpty()) {
            return null;
        }
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(string))
                .findFirst()
                .orElse(null);
    }
}
