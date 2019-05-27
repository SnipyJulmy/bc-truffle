package ch.snipy.bc.node.call;

import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.node.expression.BcFunctionLiteralNode;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

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
