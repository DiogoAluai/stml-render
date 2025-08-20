package daluai.sttp.sttp_render.rendering;

import daluai.sttp.sttp_render.simple_text.SimpleTextNode;
import jcurses.system.Toolkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static daluai.sttp.sttp_render.simple_text.Alignment.HORIZONTAL;

public class SimpleTextEngine {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleTextEngine.class);

    private final SimpleTextRenderer renderer;
    private final long framerateMillis;

    /**
     * Constructor
     *
     * @param framerateMillis framerate in millis form, i.e. how many milliseconds between frames
     */
    public SimpleTextEngine(long framerateMillis) {
        this.framerateMillis = framerateMillis;
        this.renderer = new SimpleTextRenderer();
    }

    /**
     * Synchronous engine start
     */
    public void start(SimpleTextNode head) {
        Runtime.getRuntime().addShutdownHook(new Thread(SimpleTextEngine::gracefullyShutdown));
        Toolkit.init();

        List<SimpleTextNode> childNodes = head.getChildNodes();

        double totalWeight = childNodes.stream()
                .mapToDouble(SimpleTextNode::getWeight)
                .sum();

        if (childNodes.isEmpty() || totalWeight <= 0) {
            LOG.info("Empty");
            return;
        }

        if (HORIZONTAL.equals(head.getAlignment())) {
            throw new RuntimeException("Not implemented");
        }

        List<Double> nodeWeights = childNodes.stream()
                .map(x -> x.getWeight() / totalWeight)
                .toList();

        try {
            LOG.info("Starting engine");
            engineLoop(head, nodeWeights);
        } finally {
            gracefullyShutdown();
        }
    }

    private static void gracefullyShutdown() {
        LOG.info("Shutting down gracefully");
        Toolkit.shutdown();
    }

    private void engineLoop(SimpleTextNode head, List<Double> nodeWeights) {
        while (true) {
            long start = System.currentTimeMillis();
            List<String> screen = renderer.render(head, nodeWeights);
            throttledSleepMillis(start);
            ToolkitUtils.drawScreen(screen);
        }
    }


    private void throttledSleepMillis(long frameStartMillis) {
        long nextFrameMillis = frameStartMillis + framerateMillis;
        long remaining;
        while ((remaining = nextFrameMillis - System.currentTimeMillis()) > 0) {
            safeSleep(remaining);
        }
    }

    private static void safeSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOG.error("Sleep interrupted", e);
        }
    }
}
