package daluai.stml.stml_render.parse;

public class ValueParser {

    public String parseText(String text) {
        String newLineSurroundedWithWhiteSpacesRegex = "\\s*\\R\\s*";
        return text.trim()
                .replace("\\n", "\n")
                .replaceAll(newLineSurroundedWithWhiteSpacesRegex, "\n");
    }
}
