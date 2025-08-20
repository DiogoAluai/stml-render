package daluai.sttp.sttp_render;

import daluai.sttp.sttp_render.parse.STMLParser;
import daluai.sttp.sttp_render.rendering.SimpleTextEngine;
import daluai.sttp.sttp_render.simple_text.SimpleTextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * Renders xml stml file, which is received as first and only argument.
 */
public class RenderApp {

    public static final double FRAMES_PER_SECOND = Double.parseDouble(System.getProperty("fps", "60"));
    public static final long FRAMERATE_MILLIS = (long) (1_000 / FRAMES_PER_SECOND);

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

        SimpleTextNode headNode = STMLParser.parse(xmlFileName);
        new SimpleTextEngine(FRAMERATE_MILLIS).start(headNode);
    }

}

