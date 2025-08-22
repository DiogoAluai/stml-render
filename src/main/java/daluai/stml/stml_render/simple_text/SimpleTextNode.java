package daluai.stml.stml_render.simple_text;

import daluai.stml.stml_render.data.BashDataLoader;
import daluai.stml.stml_render.data.DataLoader;
import org.w3c.dom.Attr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static daluai.stml.stml_render.simple_text.SimpleTextNodeType.UNSPECIFIED;

/**
 * STML Node. Text is atomic, to handle asynchronous set during render loop.
 */
public class SimpleTextNode {

    private static final int DEFAULT_WEIGHT = 1;
    private static final Alignment DEFAULT_ALIGNMENT = Alignment.VERTICAL;

    private List<SimpleTextNode> childNodes;

    private SimpleTextNodeType nodeType;
    private AtomicReference<String> text;
    private List<Attr> attributes;

    public SimpleTextNode() {
        this.nodeType = UNSPECIFIED;
        this.text = new AtomicReference<>("");
        this.childNodes = new ArrayList<>();
    }

    public List<SimpleTextNode> getChildNodes() {
        return childNodes;
    }

    public void addChildNode(SimpleTextNode node) {
        childNodes.add(node);
    }

    public void setChildNodes(List<SimpleTextNode> childNodes) {
        this.childNodes = childNodes;
    }

    public SimpleTextNodeType getNodeType() {
        if (nodeType == UNSPECIFIED) {
            throw new IllegalStateException("Accessing " + UNSPECIFIED + " node type");
        }
        return nodeType;
    }

    public void setNodeType(SimpleTextNodeType nodeType) {
        this.nodeType = nodeType;
    }

    public String getText() {
        return text.get();
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public List<Attr> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attr> attributes) {
        this.attributes = attributes;
    }

    public int getWeight() {
        return getAttributeValue(SimpleTextAttribute.WEIGHT, Integer::parseInt, DEFAULT_WEIGHT);
    }

    public Alignment getAlignment() {
        return getAttributeValue(SimpleTextAttribute.ALIGNMENT, Alignment::parse, DEFAULT_ALIGNMENT);
    }

    public String getDataScript() {
        return getAttributeValue(SimpleTextAttribute.DATA, Function.identity(), null);
    }

    public DataLoader getDataLoader() {
        String dataScript = getDataScript();
        if (dataScript == null || dataScript.isEmpty()) {
            return null;
        }
        //todo encapsulate logic
        return new BashDataLoader(new File(dataScript.split(":")[1]));
    }

    private <T> T getAttributeValue(SimpleTextAttribute attribute, Function<String, T> parsingFunction, T defaultValue) {
        //todo improve during parsing: so that we have safety at this point
        Optional<Attr> attrOptional = attributes.stream()
                .filter(attr -> attribute.name().equalsIgnoreCase(attr.getName()))
                .findFirst();
        if (attrOptional.isEmpty()) {
            return defaultValue;
        }

        return parsingFunction.apply(attrOptional.get().getValue());
    }
}
