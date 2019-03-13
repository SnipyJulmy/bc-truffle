package ch.snipy.node.statement;

import ch.snipy.node.BcExpressionNode;
import ch.snipy.node.BcStatementNode;
import com.oracle.truffle.api.frame.VirtualFrame;

public final class BcReturnNode extends BcStatementNode {
    @Child private BcExpressionNode valueNode;

    public BcReturnNode(BcExpressionNode valueNode) {
        this.valueNode = valueNode;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        Object result;
        if (valueNode != null) {
            result = valueNode.executeGeneric(frame);
        } else {
            // bc specify that if no return value is provide, the return value is 0
            result = (double) 0;
        }
        throw new BcReturnException(result);
    }
}
