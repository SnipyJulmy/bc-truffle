package ch.snipy.bc.node.call;

import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.node.expression.BcFunctionLiteralNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public final class BcInvokeNode extends BcExpressionNode {

    @Children private final BcExpressionNode[] argumentNodes;
    @Child private BcFunctionLiteralNode functionNode;
    @Child private BcDispatchNode dispatchNode;

    public BcInvokeNode(BcFunctionLiteralNode functionNode, BcExpressionNode[] argumentsNode) {
        this.functionNode = functionNode;
        this.argumentNodes = argumentsNode;
        this.dispatchNode = BcDispatchNodeGen.create();
    }

    @Override
    @ExplodeLoop
    public boolean executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
        Object res = executeBoundary(frame.materialize());
        if (res instanceof Boolean) return (boolean) res;
        if (res instanceof Long) return ((long) res) != 0;
        if (res instanceof Double) return ((double) res) != 0.0;
        if (res instanceof BcBigNumber) return ((BcBigNumber) res).booleanValue();
        return super.executeBoolean(frame);
    }

    @Override
    @ExplodeLoop
    public Object executeGeneric(VirtualFrame frame) {
        return executeBoundary(frame.materialize());
    }

    @TruffleBoundary
    private Object executeBoundary(MaterializedFrame frame) {
        Object function = functionNode.executeGeneric(frame);
        CompilerAsserts.compilationConstant(argumentNodes.length);

        Object[] args = new Object[argumentNodes.length];
        for (int i = 0; i < argumentNodes.length; i++) {
            args[i] = argumentNodes[i].executeGeneric(frame);
        }
        return dispatchNode.executeDispatch(
                function,
                args
        );
    }

    public String getIdentifier() {
        return functionNode.getIdentifier();
    }
}
