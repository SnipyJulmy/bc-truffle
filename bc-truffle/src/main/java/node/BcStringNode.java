package node;

public class BcStringNode extends BcExpressionNode {
    private final String value;

    public BcStringNode(String value) {
        this.value = value;
    }
}
