package node;

import com.oracle.truffle.api.frame.VirtualFrame;

public class BcNumberNode extends BcExpressionNode {
    private final double value;

    public BcNumberNode(double value) {
        this.value = value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return null;
    }
}
