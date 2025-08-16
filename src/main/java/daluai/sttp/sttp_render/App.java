package daluai.sttp.sttp_render;

import daluai.sttp.sttp_render.parse.STMLParser;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 *
 */
public class App {

    public static void main( String[] args ) throws IOException, SAXException {
        String xmlFileName = args[0];
        if (xmlFileName == null || xmlFileName.isEmpty()) {
            System.out.println("Please provided xml argument as first argument");
            return;
        }

        STMLParser.parse(xmlFileName);
    }


}
