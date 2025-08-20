package daluai.sttp.sttp_render.rendering;

import jcurses.system.CharColor;
import jcurses.system.Toolkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public final class ToolkitUtils {

    public static final int DEFAULT_WEIGHT = 80;
    public static final int DEFAULT_HEIGHT = 24;

    private static final Logger LOG = LoggerFactory.getLogger(ToolkitUtils.class);
    private static final CharColor COLOR = new CharColor(CharColor.WHITE, CharColor.BLACK);

    private static final char HORIZONTAL_LINE_CHAR = '-';
    private static final char VERTICAL_LINE_CHAR = '|';
    private static final char TOP_LEFT_CHAR = '+';
    private static final char TOP_RIGHT_CHAR = '+';
    private static final char BOTTOM_LEFT_CHAR = '+';
    private static final char BOTTOM_RIGHT_CHAR = '+';
    private static final char INNER_LINE_EMPTY_CHAR = ' ';
    public static final int MIN_BOX_VERTICAL_LENGTH = 2;


    public static int safeScreenWidth() {
        try {
            return Math.max(1, Toolkit.getScreenWidth());
        } catch (Throwable t) {
            LOG.warn("Unable to get screen width, defaulting to {}", DEFAULT_HEIGHT, t);
            return DEFAULT_WEIGHT;
        }
    }

    public static int safeScreenHeight() {
        try {
            return Math.max(1, Toolkit.getScreenHeight());
        } catch (Throwable t) {
            LOG.warn("Unable to get screen height, defaulting to {}", DEFAULT_HEIGHT, t);
            return DEFAULT_HEIGHT;
        }
    }

    public static List<String> createTextBoxStrings(int cols, int rows, String text) {
        if (rows < MIN_BOX_VERTICAL_LENGTH) {
            // provide minimal box
            return createBoxStrings(cols, MIN_BOX_VERTICAL_LENGTH);
        }

        int innerStringsCount = rows - 2;
        if (text == null || text.isEmpty() || innerStringsCount == 0) {
            return createBoxStrings(cols, rows);
        }

        // favor first half when innerStringsCount is even
        int innerIndexOfText = (innerStringsCount - 1) / 2;

        String top = createBoxTopString(cols);
        String bottom = createBoxBottomString(cols);
        List<String> boxInnerStrings = createBoxInnerStrings(innerStringsCount - 1, cols);
        var firstHalf = boxInnerStrings.subList(0, innerIndexOfText);
        var secondHalf = boxInnerStrings.subList(innerIndexOfText, boxInnerStrings.size());

        List<String> box = new ArrayList<>(rows);
        box.add(top);
        box.addAll(firstHalf);
        box.add(createTextLine(cols, text));
        box.addAll(secondHalf);
        box.add(bottom);

        return box;
    }

    /**
     * Create box inner line with text in the middle, favoring left on odd length
     */
    private static String createTextLine(int cols, String text) {
        int supportedTextLength = cols - 2;
        if (text.length() > supportedTextLength) {
            String etc = "...";
            int textSnippetLength = supportedTextLength - etc.length();
            text = text.substring(0, textSnippetLength) + etc;
        }
        int totalPadding = cols - 2 - text.length();
        int leftPadding = totalPadding / 2;
        int rightPadding = leftPadding + totalPadding % 2;
        return VERTICAL_LINE_CHAR
                + String.valueOf(INNER_LINE_EMPTY_CHAR).repeat(leftPadding)
                + text
                + String.valueOf(INNER_LINE_EMPTY_CHAR).repeat(rightPadding)
                + VERTICAL_LINE_CHAR;
    }

    /**
     * Create horizontal box string, i.e. it will fill the screen from the left to the right
     */
    public static List<String> createBoxStrings(int cols, int rows) {
        String top = createBoxTopString(cols);
        String bottom = createBoxBottomString(cols);
        List<String> boxInnerStrings = createBoxInnerStrings(rows  - 2, cols);

        List<String> box = new ArrayList<>(rows);
        box.add(top);
        box.addAll(boxInnerStrings);
        box.add(bottom);

        return box;
    }

    public static void drawScreen(List<String> screen) {
        if (screen == null || screen.isEmpty()) {
            return;
        }

        // assuming cols and rows are consistent
        int cols = screen.get(0).length();
        int rows = screen.size();

        IntStream.range(0, rows).forEach(i -> {
            Toolkit.printString(screen.get(i), 0, i, COLOR);
        });
    }


    public static void drawFullScreenBox(int cols, int rows) {
        if (cols < 2 || rows < 2) {
            return;
        }

        String top = createBoxTopString(cols);
        String bottom = createBoxBottomString(cols);

        Toolkit.printString(top, 0, 0, COLOR);
        printBoxInnerRows(cols, rows);
        Toolkit.printString(bottom, 0, rows - 1, COLOR);
    }

    // Print inner rows
    private static void printBoxInnerRows(int cols, int rows) {
        String verticalLineChar = String.valueOf(VERTICAL_LINE_CHAR);
        for (int y = 1; y < rows - 1; y++) {
            Toolkit.printString(verticalLineChar, 0, y, COLOR);
            Toolkit.printString(verticalLineChar, cols - 1, y, COLOR);
        }
    }

    private static List<String> createBoxInnerStrings(int lines, int cols) {
        String verticalLineChar = String.valueOf(VERTICAL_LINE_CHAR);
        String innerSpace = String.valueOf(INNER_LINE_EMPTY_CHAR).repeat(cols - 2);
        List<String> innerStrings = new ArrayList<>();
        IntStream.range(0, lines).forEach(i -> {
            String boxInnerLine = createBoxInnerString(verticalLineChar, innerSpace);
            innerStrings.add(boxInnerLine);
        });
        return innerStrings;
    }

    private static String createBoxInnerString(String verticalLineChar, String innerSpace) {
        return verticalLineChar
                + innerSpace
                + verticalLineChar;
    }

    private static String createBoxBottomString(int cols) {
        return BOTTOM_LEFT_CHAR
                + String.valueOf(HORIZONTAL_LINE_CHAR).repeat(cols - 2)
                + BOTTOM_RIGHT_CHAR;
    }

    private static String createBoxTopString(int cols) {
        return TOP_LEFT_CHAR
                + String.valueOf(HORIZONTAL_LINE_CHAR).repeat(cols - 2)
                + TOP_RIGHT_CHAR;
    }

    private ToolkitUtils() {
    }
}
