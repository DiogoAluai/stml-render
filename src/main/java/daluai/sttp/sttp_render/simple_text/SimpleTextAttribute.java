package daluai.sttp.sttp_render.simple_text;

import daluai.sttp.sttp_render.parse.SimpleTextParsingException;

import java.util.Arrays;

public enum SimpleTextAttribute {

    VERSION,
    ALIGNMENT,
    WEIGHT,
    COLOR;

    public static void validate(String attributeString) {
        Arrays.stream(values())
                .filter(attribute -> attribute.name().equals(attributeString.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new SimpleTextParsingException(
                        "Could not validate '" + attributeString + "' as a " + SimpleTextAttribute.class.getSimpleName()));
    }
}
