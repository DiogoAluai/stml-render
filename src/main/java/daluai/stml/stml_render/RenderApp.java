package daluai.stml.stml_render;

import daluai.stml.stml_render.parse.STMLParser;
import daluai.stml.stml_render.rendering.SimpleTextEngine;
import daluai.stml.stml_render.simple_text.SimpleTextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * Renders xml stml file, which is received as first and only argument.
 */
public class RenderApp {

    public static final double FRAMES_PER_SECOND = Double.parseDouble(System.getProperty("fps", "60"));
    public static final long FRAME_RATE_MILLIS = (long) (1_000 / FRAMES_PER_SECOND);
    public static final long DATA_REFRESH_RATE_MILLIS = 3000;

    private static final Logger LOG = LoggerFactory.getLogger(RenderApp.class);

    public static void main( String[] args ) throws IOException, SAXException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Must receive exactly one argument, the xml filename");
        }
        String xmlFileName = args[0];

        if (xmlFileName == null || xmlFileName.isEmpty()) {
            LOG.error("Please provided xml argument as first argument");
            return;
        }

        SimpleTextNode headNode = new STMLParser(xmlFileName).parse();
        new SimpleTextEngine(FRAME_RATE_MILLIS, DATA_REFRESH_RATE_MILLIS).start(headNode);
    }

}

