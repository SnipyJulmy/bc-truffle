package ch.snipy.bc.node.controlflow;

import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.node.BcStatementNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.profiles.BranchProfile;

@SuppressWarnings("WeakerAccess")
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
            if (endLoopNode != null) endLoopNode.executeVoid(frame);
            return true;
        } catch (BcContinueException e) {
            continueTaken.enter();
            if (endLoopNode != null) endLoopNode.executeVoid(frame);
            return true;
        } catch (BcBreakException e) {
            breakTaken.enter();
            return false;
        }
    }

    private boolean evaluateCondition(VirtualFrame frame) {
        if (conditionNode == null)
            return true;
        try {
            return conditionNode.executeLong(frame) != 0L;
        } catch (UnexpectedResultException e) {
            Object res = conditionNode.executeGeneric(frame);
            if (res instanceof BcBigNumber) {
                return ((BcBigNumber) res).booleanValue();
            } else {
                throw new UnsupportedSpecializationException(this, new Node[]{conditionNode}, e.getResult());
            }
        }
    }
}
