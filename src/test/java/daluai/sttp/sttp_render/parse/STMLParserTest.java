package daluai.sttp.sttp_render.parse;

import daluai.sttp.sttp_render.simple_text.SimpleTextNodeType;
import daluai.sttp.sttp_render.TestUtils;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

public class STMLParserTest {

    private final TestUtils testUtils = TestUtils.createUtils(STMLParserTest.class);

    @Test
    public void parsing() throws IOException, SAXException {
        var res = testUtils.getResourcePath("simple_grid.xml");
        var headNode = STMLParser.parse(res.getPath());

        assertEquals(headNode.getText(), "");
        assertEquals(headNode.getNodeType(), SimpleTextNodeType.STML);
        assertEquals(headNode.getAttributes().size(), 1);
        assertEquals(headNode.getAttributes().get(0).getName(), "version");
        assertEquals(headNode.getAttributes().get(0).getValue(), "1");

        assertEquals(headNode.getChildNodes().size(), 1);
        var wrapperGrid = headNode.getChildNodes().get(0);

        assertEquals(wrapperGrid.getChildNodes().size(), 6);
        assertEquals(wrapperGrid.getNodeType(), SimpleTextNodeType.GRID);
        assertEquals(wrapperGrid.getAttributes().size(), 1);
        assertEquals(wrapperGrid.getAttributes().get(0).getName(), "color");
        assertEquals(wrapperGrid.getAttributes().get(0).getValue(), "dsa");

        var text1 = wrapperGrid.getChildNodes().get(0);
        var text2 = wrapperGrid.getChildNodes().get(1);
        var childGrid = wrapperGrid.getChildNodes().get(2);
        var text3 = wrapperGrid.getChildNodes().get(3);
        var text4 = wrapperGrid.getChildNodes().get(4);
        var text5 = wrapperGrid.getChildNodes().get(5);

        assertEquals(text1.getText(), "Hello1");
        assertEquals(text2.getText(), "Hello2");
        // text for grid should be ignored
        assertEquals(childGrid.getText(), "");
        assertEquals(childGrid.getChildNodes().size(), 1);
        assertEquals(childGrid.getChildNodes().get(0).getText(), "nice text");
        assertEquals(text3.getText(), "Hello3");
        assertEquals(text4.getText(), "I am\nIRON\nMAN!!");
        assertEquals(text5.getText(), "New\nline");
    }
}