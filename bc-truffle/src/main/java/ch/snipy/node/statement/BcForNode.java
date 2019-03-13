package ch.snipy.node.statement;

import ch.snipy.node.BcExpressionNode;
import ch.snipy.node.BcStatementNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;

public final class BcForNode extends BcStatementNode {
    @Child private LoopNode loopNode;
    @Child private BcExpressionNode initNode;

    public BcForNode(LoopNode loopNode, BcExpressionNode initNode) {
        this.loopNode = loopNode;
        this.initNode = initNode;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        initNode.executeVoid(frame);
        loopNode.executeLoop(frame);
    }
}
