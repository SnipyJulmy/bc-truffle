package node;

import com.oracle.truffle.api.frame.VirtualFrame;

public class BcStringNode extends BcExpressionNode {
    private final String value;

    public BcStringNode(String value) {
        this.value = value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return null;
    }
}
