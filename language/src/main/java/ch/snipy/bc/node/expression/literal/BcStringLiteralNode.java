package ch.snipy.bc.node.expression.literal;

import ch.snipy.bc.node.BcExpressionNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

/**
 * Constant literal for a String value
 */
@NodeInfo(shortName = "const")
public class BcStringLiteralNode extends BcExpressionNode {
    private final String value;

    public BcStringLiteralNode(String value) {
        this.value = value;
    }

    @Override
    public String executeString(VirtualFrame frame) {
        return value;
    }

    @Override
    public String executeGeneric(VirtualFrame frame) {
        return value;
    }
}