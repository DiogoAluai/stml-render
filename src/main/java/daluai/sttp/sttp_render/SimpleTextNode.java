package daluai.sttp.sttp_render;

import org.w3c.dom.Attr;

import java.util.ArrayList;
import java.util.List;

import static daluai.sttp.sttp_render.SimpleTextNodeType.UNSPECIFIED;

public class SimpleTextNode {

    private List<SimpleTextNode> childNodes;

    private SimpleTextNodeType nodeType;
    private String text;
    private List<Attr> attributes;

    public SimpleTextNode() {
        nodeType = UNSPECIFIED;
        this.text = "";
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
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Attr> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attr> attributes) {
        this.attributes = attributes;
    }
}
