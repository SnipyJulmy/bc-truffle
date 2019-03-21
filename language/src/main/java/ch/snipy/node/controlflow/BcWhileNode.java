package ch.snipy.node.controlflow;

import ch.snipy.node.BcExpressionNode;
import ch.snipy.node.BcStatementNode;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;

public final class BcWhileNode extends BcStatementNode {
    @Child private LoopNode loopNode;

    public BcWhileNode(BcExpressionNode conditionNode, BcStatementNode bodyNode) {
        this.loopNode = Truffle.getRuntime().createLoopNode(
                new BcWhileRepeatingNode(conditionNode, bodyNode)
        );
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        loopNode.executeLoop(frame);
    }
}
