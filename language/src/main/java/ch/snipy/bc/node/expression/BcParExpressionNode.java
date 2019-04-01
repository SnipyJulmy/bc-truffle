package ch.snipy.bc.node.expression;

import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class BcParExpressionNode extends BcExpressionNode {

    @Child private BcExpressionNode expression;

    public BcParExpressionNode(BcExpressionNode expression) {
        this.expression = expression;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return expression.executeGeneric(frame);
    }

    @Override
    public BcBigNumber executeBigNumber(VirtualFrame frame) throws UnexpectedResultException {
        return expression.executeBigNumber(frame);
    }

    @Override
    public String executeString(VirtualFrame frame) throws UnexpectedResultException {
        return expression.executeString(frame);
    }

    @Override
    public Object[] executeObjectArray(VirtualFrame frame) throws UnexpectedResultException {
        return expression.executeObjectArray(frame);
    }
}
