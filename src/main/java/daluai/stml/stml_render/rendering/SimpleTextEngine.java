package daluai.stml.stml_render.rendering;

import daluai.stml.stml_render.data.DataLoader;
import daluai.stml.stml_render.parse.ValueParser;
import daluai.stml.stml_render.simple_text.SimpleTextNode;
import jcurses.system.Toolkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static daluai.stml.stml_render.simple_text.Alignment.HORIZONTAL;

public class SimpleTextEngine {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleTextEngine.class);

    private final long framerateMillis;
    private final long dataRefreshRateMillis;
    private final SimpleTextRenderer renderer;
    private final ExecutorService executorService;
    private final ValueParser valueParser;

    /**
     * Constructor
     *
     * @param framerateMillis framerate in millis form, i.e. how many milliseconds between frames
     */
    public SimpleTextEngine(long framerateMillis, long dataRefreshRateMillis) {
        this.framerateMillis = framerateMillis;
        this.dataRefreshRateMillis = dataRefreshRateMillis;
        this.renderer = new SimpleTextRenderer();
        this.executorService = Executors.newSingleThreadExecutor();
        this.valueParser = new ValueParser();
    }

    /**
     * Synchronous engine start
     */
    public void start(SimpleTextNode head) {
        Runtime.getRuntime().addShutdownHook(new Thread(this::gracefullyShutdown));
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
            executorService.execute(() -> dataLoadLoop(head));
            engineLoop(head, nodeWeights);
        } finally {
            gracefullyShutdown();
        }
    }

    private void gracefullyShutdown() {
        LOG.info("Shutting down gracefully");
        Toolkit.shutdown();
    }

    private void dataLoadLoop(SimpleTextNode head) {
        while (true) {
            long start = System.currentTimeMillis();
            head.getChildNodes().forEach(node -> {
                DataLoader dataLoader = node.getDataLoader();
                if (dataLoader != null) {
                    String textData = dataLoader.load();
                    node.setText(valueParser.parseText(textData));
                }
            });

            throttledSleepMillis(start, dataRefreshRateMillis);
        }

    }

    private void engineLoop(SimpleTextNode head, List<Double> nodeWeights) {
        while (true) {
            long start = System.currentTimeMillis();
            List<String> screen = renderer.render(head, nodeWeights);
            throttledSleepMillis(start, framerateMillis);
            ToolkitUtils.drawScreen(screen);
        }
    }


    private void throttledSleepMillis(long frameStartMillis, long framerateMillis) {
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
