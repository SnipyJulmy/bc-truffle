package ch.snipy.bc.node.expression.literal;

import ch.snipy.bc.BcLanguage;
import ch.snipy.bc.node.BcExpressionNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

/**
 * Constant literal for a double value
 */
@NodeInfo(shortName = "const")
public class BcDoubleLiteralNode extends BcExpressionNode {

    private final double value;

    public BcDoubleLiteralNode(double value) {
        this.value = value;
    }

    @Override
    public boolean executeBoolean(VirtualFrame frame) {
        return value != 0.0;
    }

    @Override
    public long executeLong(VirtualFrame frame) throws UnexpectedResultException {
        if (BcLanguage.getCurrentContext().getScale() == 0)
            return (long) value;
        return super.executeLong(frame);
    }

    @Override
    public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
        return super.executeDouble(frame);
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
