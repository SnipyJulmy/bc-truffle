package ch.snipy.bc.node;


import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

@NodeInfo(description = "Base class for any expression ch.snipy.node")
@TypeSystemReference(BcTypes.class)
public abstract class BcExpressionNode extends BcStatementNode {

    // execute this when no specialization is possible --> most possible generalization
    public abstract Object executeGeneric(VirtualFrame frame);

    // the return getValue is not needed
    @Override
    public void executeVoid(VirtualFrame frame) {
        executeGeneric(frame);
    }

    public BcBigNumber executeBigNumber(VirtualFrame frame) throws UnexpectedResultException {
        return BcTypesGen.expectBcBigNumber(executeGeneric(frame));
    }

    public String executeString(VirtualFrame frame) throws UnexpectedResultException {
        return BcTypesGen.expectString(executeGeneric(frame));
    }

    public Object[] executeObjectArray(VirtualFrame frame) throws UnexpectedResultException {
        return BcTypesGen.expectObjectArray(executeGeneric(frame));
    }
}
