package ch.snipy.bc.node.controlflow;

import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.node.BcStatementNode;
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
            // bc specify that if no return getValue is provide, the return getValue is 0
            result = 0;
        }
        throw new BcReturnException(result);
    }
}
