package daluai.stml.stml_render.rendering;

import daluai.stml.stml_render.simple_text.SimpleTextNode;
import daluai.stml.stml_render.simple_text.SimpleTextNodeType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class SimpleTextRenderer {

    private static final int MIN_BOX_HEIGHT = 3;

    public List<String> render(SimpleTextNode headNode, List<Double> nodeWeights) {
        List<SimpleTextNode> childNodes = headNode.getChildNodes();

        int rows = ToolkitUtils.safeScreenHeight();
        int cols = ToolkitUtils.safeScreenWidth();

        var verticalLengths = getVerticalLengths(nodeWeights, rows);
        if (verticalLengths.stream().reduce(0L, Long::sum) != rows) {
            // todo: this assert could be better coupled with objects. But just maybe, this is good enough
            throw new IllegalStateException("Vertical lengths do no match screen rows");
        }

        List<String> screen = new ArrayList<>(rows);
        IntStream.range(0, childNodes.size()).forEach(i -> {
            var node = childNodes.get(i);
            int verticalLength = Math.toIntExact(verticalLengths.get(i));
            List<String> box;
            if (SimpleTextNodeType.TEXT.equals(node.getNodeType())) {
                box = ToolkitUtils.createTextBoxStrings(cols, verticalLength, node.getText()); // using full width
            } else {
                box = ToolkitUtils.createBoxStrings(cols, verticalLength); // using full width
            }
            screen.addAll(box);
        });
        return screen;
    }

    private static List<Long> getVerticalLengths(List<Double> nodeWeights, int height) {
        List<Long> verticalLengths = new ArrayList<>();
        nodeWeights.stream()
                .map(ratioedLength -> Math.max(MIN_BOX_HEIGHT, Math.round(ratioedLength * height)))
                .forEach(verticalLengths::add);
        correctForRounding(height, verticalLengths);
        return verticalLengths;
    }

    /**
     * After going through this process, the list will be changed to achieve consistency with height.
     * The biggest vertical length will get added or subtracted upon
     *
     * @param height height
     * @param verticalLengths the list
     */
    private static void correctForRounding(int height, List<Long> verticalLengths) {
        long totalLength = verticalLengths.stream().mapToLong(x -> x).sum();
        if (totalLength == height) {
            return;
        }
        long lengthDiff = height - totalLength;
        int biggestLengthIndex = -1;
        for (int i = 0; i < verticalLengths.size(); i++) {
            long thisLength = verticalLengths.get(i);
            if (thisLength > biggestLengthIndex) {
                biggestLengthIndex = i;
            }
        }
        verticalLengths.set(biggestLengthIndex, verticalLengths.get(biggestLengthIndex) + lengthDiff);
    }
}
