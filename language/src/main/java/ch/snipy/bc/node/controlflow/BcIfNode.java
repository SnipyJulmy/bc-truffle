package ch.snipy.bc.node.controlflow;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.node.BcStatementNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.profiles.ConditionProfile;

public final class BcIfNode extends BcStatementNode {
    private final ConditionProfile conditionProfile = ConditionProfile.createCountingProfile();
    @Child private BcExpressionNode conditionNode;
    @Child private BcStatementNode trueNode;
    @Child private BcStatementNode falseNode;

    public BcIfNode(BcExpressionNode conditionNode, BcStatementNode trueNode, BcStatementNode falseNode) {
        this.conditionNode = conditionNode;
        this.trueNode = trueNode;
        this.falseNode = falseNode;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        try {
            if (conditionProfile.profile(conditionNode.executeLong(frame) != 0L))
                trueNode.executeVoid(frame);
            else {
                if (falseNode != null) falseNode.executeVoid(frame);
            }
        } catch (UnexpectedResultException e) {
            throw BcException.typeError(this, e.getResult());
        }
    }
}
