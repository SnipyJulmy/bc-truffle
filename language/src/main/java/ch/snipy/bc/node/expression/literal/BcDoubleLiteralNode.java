package ch.snipy.bc.node.expression.literal;

import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.node.BcExpressionNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

@NodeInfo(shortName = "const")
public class BcDoubleLiteralNode extends BcExpressionNode {
    private final double value;

    public BcDoubleLiteralNode(double value) {
        this.value = value;
    }

    @Override
    public String executeString(VirtualFrame frame) throws UnexpectedResultException {
        return "" + value;
    }

    @Override
    public boolean executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
        return value != 0;
    }

    @Override
    public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
        return value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }
}
