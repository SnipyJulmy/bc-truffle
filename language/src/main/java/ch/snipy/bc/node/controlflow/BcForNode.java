package ch.snipy.bc.node.controlflow;

import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.node.BcStatementNode;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;

public final class BcForNode extends BcStatementNode {
    @Child private LoopNode loopNode;
    @Child private BcExpressionNode initNode;

    public BcForNode(BcExpressionNode initNode,
                     BcExpressionNode conditionNode,
                     BcExpressionNode endLoopNode,
                     BcStatementNode bodyNode) {
        this.initNode = initNode;
        this.loopNode = Truffle.getRuntime().createLoopNode(
                new BcForRepeatingNode(
                        conditionNode,
                        endLoopNode,
                        bodyNode
                )
        );
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        if (initNode != null) initNode.executeVoid(frame);
        loopNode.executeLoop(frame);
    }
}
