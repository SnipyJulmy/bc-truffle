package node;


import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

@NodeInfo(description = "Base class for any expression node")
@TypeSystemReference(BcTypes.class)
public abstract class BcExpressionNode extends BcStatementNode {

    public abstract Object executeGeneric(VirtualFrame frame);

    public String executeString(VirtualFrame frame) throws UnexpectedResultException {
        return BcTypesGen.expectString(executeGeneric(frame));
    }

    public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
        return BcTypesGen.expectDouble(executeGeneric(frame));
    }
}
