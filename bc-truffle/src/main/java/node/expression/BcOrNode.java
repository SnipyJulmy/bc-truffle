package node.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import node.BcBinaryNode;
import node.BcExpressionNode;

public class BcOrNode extends BcShortCircuitNode {

    public BcOrNode(BcExpressionNode left, BcExpressionNode right) {
        super(left, right);
    }

    @Override
    protected boolean isEvaluateRight(boolean leftValue) {
        return false;
    }

    @Override
    protected boolean execute(boolean leftValue, boolean rightValue) {
        return false;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return null;
    }
}
