package ch.snipy.bc.node.call;

import ch.snipy.bc.node.BcExpressionNode;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

public final class BcInvokeNode extends BcExpressionNode {

    @Children private final BcExpressionNode[] argumentNodes;
    @Child private BcExpressionNode functionNode;

    public BcInvokeNode(BcExpressionNode functionNode, BcExpressionNode[] argumentsNode) {
        this.functionNode = functionNode;
        this.argumentNodes = argumentsNode;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return null;
    }

    /* TODO
    @Override
    @ExplodeLoop
    public Object executeGeneric(VirtualFrame frame) {
        Object function = functionNode.executeGeneric(frame);
        CompilerAsserts.compilationConstant(argumentNodes.length);

        Object[] argumentValues = new Object[argumentNodes.length];
        for (int i = 0; i < argumentNodes.length; i++) {
            argumentValues[i] = argumentNodes[i].executeGeneric(frame);
        }
    }
    */
}
