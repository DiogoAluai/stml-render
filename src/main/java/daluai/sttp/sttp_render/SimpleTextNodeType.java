package daluai.sttp.sttp_render;

import java.util.Arrays;

public enum SimpleTextNodeType {
    STML,
    GRID,
    TEXT,
    UNSPECIFIED;

    public static SimpleTextNodeType parse(String tagName) {
        return Arrays.stream(values())
                .filter(nodeType -> nodeType != UNSPECIFIED)
                .filter(nodeType -> nodeType.name().equals(tagName.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new SimpleTextParsingException(""));
    }
}
