package ch.snipy.bc.node.call;

import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.node.expression.BcFunctionLiteralNode;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import java.util.Arrays;

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
        Object function = functionNode.executeGeneric(frame);
        CompilerAsserts.compilationConstant(argumentNodes.length);
        return dispatchNode.executeDispatch(
                function,
                Arrays.stream(argumentNodes).map(n -> n.executeGeneric(frame)).toArray()
        );
    }
}
