package ch.snipy.bc.node.expression.literal;

import ch.snipy.bc.node.BcExpressionNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

/**
 *  Constant literal for a long literal
 */
@NodeInfo(shortName = "const")
public class BcLongLiteralNode extends BcExpressionNode {

    private final long value;

    public BcLongLiteralNode(long value) {
        this.value = value;
    }

    @Override
    public boolean executeBoolean(VirtualFrame frame) {
        return value != 0L;
    }

    @Override
    public long executeLong(VirtualFrame frame) {
        return value;
    }

    @Override
    public double executeDouble(VirtualFrame frame) {
        return (double) value;
    }

    @Override
    public String executeString(VirtualFrame frame) {
        return "" + value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }
}
