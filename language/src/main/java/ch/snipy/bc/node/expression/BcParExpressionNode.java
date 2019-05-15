package ch.snipy.bc.node.expression;

import ch.snipy.bc.node.BcExpressionNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class BcParExpressionNode extends BcExpressionNode {

    @Child private BcExpressionNode expression;

    public BcParExpressionNode(BcExpressionNode expression) {
        this.expression = expression;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        expression.executeVoid(frame);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return expression.executeGeneric(frame);
    }

    @Override
    public long executeLong(VirtualFrame frame) throws UnexpectedResultException {
        return expression.executeLong(frame);
    }

    @Override
    public String executeString(VirtualFrame frame) throws UnexpectedResultException {
        return expression.executeString(frame);
    }

}
