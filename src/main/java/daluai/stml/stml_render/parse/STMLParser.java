package daluai.stml.stml_render.parse;

import daluai.stml.stml_render.simple_text.SimpleTextAttribute;
import daluai.stml.stml_render.simple_text.SimpleTextNode;
import daluai.stml.stml_render.simple_text.SimpleTextNodeType;
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

    private final DocumentBuilder builder;
    private final ValueParser valueParser;
    private final File xmlFile;

    public STMLParser(String xmlFileName) {
        this.valueParser = new ValueParser();
        this.xmlFile = validateFile(new File(xmlFileName));
        this.builder = safeCreateDocumentBuilder();
    }

    private static File validateFile(File file) {
        if (!file.exists()) {
            throw new RuntimeException("File does not exist: " + file.getAbsolutePath());
        }
        if (!file.canRead()) {
            throw new RuntimeException("Cannot read file: " + file.getAbsolutePath());
        }
        return file;
    }

    private static DocumentBuilder safeCreateDocumentBuilder() {
        final DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // default configuration cannot be wrong, therefore never throwing an exception
            throw new RuntimeException(e);
        }
        return builder;
    }

    /**
     * Return head node
     */
    public SimpleTextNode parse() throws IOException, SAXException {
        Document doc = builder.parse(xmlFile);
        doc.getDocumentElement().normalize();
        SimpleTextNode headNode = new SimpleTextNode();
        traverse(headNode, doc.getDocumentElement());
        return headNode;
    }

    void traverse(SimpleTextNode simpleTextNode, Node node) throws SimpleTextParsingException {
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
            String textValue = null;
            NodeList childNodes = node.getChildNodes();
            if (childNodes.getLength() == 1) {
                String text = childNodes.item(0).getTextContent();
                textValue = valueParser.parseText(text);
            }
            if (textValue == null && simpleTextNode.getDataScript() == null) {
                throw new SimpleTextParsingException("Got no text nor data script or text node");
            }
            simpleTextNode.setText(textValue);
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
