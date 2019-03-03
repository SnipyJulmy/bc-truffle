package node;

public class BcNumberNode extends BcExpressionNode {
    private final double value;

    public BcNumberNode(double value) {
        this.value = value;
    }
}
