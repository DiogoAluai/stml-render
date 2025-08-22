package daluai.stml.stml_render.simple_text;

import daluai.stml.stml_render.parse.SimpleTextParsingException;

import java.util.Arrays;

public enum SimpleTextAttribute {

    VERSION,
    ALIGNMENT,
    WEIGHT,
    COLOR,
    DATA; // format: "bash:some_script"

    public static void validate(String attributeString) {
        Arrays.stream(values())
                .filter(attribute -> attribute.name().equals(attributeString.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new SimpleTextParsingException(
                        "Could not validate '" + attributeString + "' as a " + SimpleTextAttribute.class.getSimpleName()));
    }
}
