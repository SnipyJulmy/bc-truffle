package node.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import node.BcExpressionNode;

@NodeInfo(shortName = "const")
public class BcDoubleLiteralNode extends BcExpressionNode {
    private final double value;

    public BcDoubleLiteralNode(double value) {
        this.value = value;
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
