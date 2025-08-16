package daluai.sttp.sttp_render.parse;

import daluai.sttp.sttp_render.SimpleTextAttribute;
import daluai.sttp.sttp_render.SimpleTextNode;
import daluai.sttp.sttp_render.SimpleTextNodeType;
import daluai.sttp.sttp_render.SimpleTextParsingException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Parser for Simple Text Markup Language
 */
public class STMLParser {

    private static final DocumentBuilder builder;

    static {
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // default configuration cannot be wrong, therefore never throwing an exception
            throw new RuntimeException(e);
        }
    }

    public static SimpleTextNode parse(String xmlFileName) throws IOException, SAXException {
        File xmlFile = new File(xmlFileName);
        Document doc = builder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        SimpleTextNode headNode = new SimpleTextNode();
        traverse(headNode, doc.getDocumentElement());
        return headNode;
    }

    static void traverse(SimpleTextNode simpleTextNode, Node node) throws SimpleTextParsingException {
        Element element = (Element) node;
        String tagName = element.getTagName();

        SimpleTextNodeType stNodeType = SimpleTextNodeType.parse(tagName);
        simpleTextNode.setNodeType(stNodeType);
        ArrayList<Attr> attributes = parseAttributesForElement(element);
        simpleTextNode.setAttributes(attributes);

        if (SimpleTextNodeType.STML == stNodeType &&
                !Objects.equals(attributes.get(0).getValue(), "1")) {
            throw new SimpleTextParsingException("Only STML version 1 is supported");
        }

        if (SimpleTextNodeType.TEXT == stNodeType) {
            String text = parseTextInsideTextNode(node);
            simpleTextNode.setText(text);
        }


        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node childItem = children.item(i);
            if (childItem.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            var childSimpleTextNode = new SimpleTextNode();
            simpleTextNode.addChildNode(childSimpleTextNode);
            traverse(childSimpleTextNode, childItem);
        }
    }

    private static String parseTextInsideTextNode(Node node) {
        NodeList childNodes = node.getChildNodes();
        if (childNodes.getLength() != 1) {
            throw new SimpleTextParsingException("Text node has children count different than 1. Count: "
                    + childNodes.getLength());
        }
        return childNodes.item(0).getTextContent().trim();
    }

    private static ArrayList<Attr> parseAttributesForElement(Element element) {
        var attributeList = new ArrayList<Attr>();
        NamedNodeMap attrs = element.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Attr attributes = (Attr) attrs.item(i);
            SimpleTextAttribute.validate(attributes.getName());
            attributeList.add(attributes);
        }
        return attributeList;
    }
}
