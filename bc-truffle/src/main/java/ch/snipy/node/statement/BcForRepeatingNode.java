package ch.snipy.node.statement;

import ch.snipy.node.BcExpressionNode;
import ch.snipy.node.BcStatementNode;
import ch.snipy.node.controlflow.BcBreakException;
import ch.snipy.node.controlflow.BcContinueException;
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.profiles.BranchProfile;

public final class BcForRepeatingNode extends Node implements RepeatingNode {

    private final BranchProfile continueTaken = BranchProfile.create();
    private final BranchProfile breakTaken = BranchProfile.create();

    @Child private BcExpressionNode conditionNode;
    @Child private BcExpressionNode endLoopNode;
    @Child private BcStatementNode bodyNode;


    public BcForRepeatingNode(BcExpressionNode conditionNode,
                              BcExpressionNode endLoopNode,
                              BcStatementNode bodyNode) {
        this.conditionNode = conditionNode;
        this.endLoopNode = endLoopNode;
        this.bodyNode = bodyNode;
    }


    @Override
    public boolean executeRepeating(VirtualFrame frame) {
        if (!evaluateCondition(frame))
            return false;
        try {
            bodyNode.executeVoid(frame);
            endLoopNode.executeVoid(frame);
            return true;
        } catch (BcContinueException e) {
            continueTaken.enter();
            return true;
        } catch (BcBreakException e) {
            breakTaken.enter();
            return true;
        }
    }

    private boolean evaluateCondition(VirtualFrame frame) {
        try {
            return conditionNode.executeBoolean(frame);
        } catch (UnexpectedResultException e) {
            throw new UnsupportedSpecializationException(this, new Node[]{conditionNode}, e.getResult());
        }
    }
}
