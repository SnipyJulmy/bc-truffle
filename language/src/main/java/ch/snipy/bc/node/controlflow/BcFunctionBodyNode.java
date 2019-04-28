package ch.snipy.bc.node.controlflow;

import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.node.BcStatementNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.profiles.BranchProfile;

import static ch.snipy.bc.runtime.BcBigNumber.ZERO;

@NodeInfo(shortName = "body")
public final class BcFunctionBodyNode extends BcExpressionNode {

    @Child private BcStatementNode bodyNode;

    private final BranchProfile exceptionTaken = BranchProfile.create();
    private final BranchProfile nullTaken = BranchProfile.create();

    public BcFunctionBodyNode(BcStatementNode bodyNode) {
        this.bodyNode = bodyNode;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        try {
            bodyNode.executeVoid(frame);
        } catch (BcReturnException e) {
            exceptionTaken.enter();
            return e.result();
        }
        nullTaken.enter();
        return "";
    }
}
