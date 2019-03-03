package node;


import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

@TypeSystemReference(BcTypes.class)
public abstract class BcExpressionNode extends BcStatementNode {

    public abstract Object executeGeneric(VirtualFrame frame);

    public void executeVoid(VirtualFrame frame) {
        executeGeneric(frame);
    }

    public long executeLong(VirtualFrame frame) throws UnexpectedResultException {
        return BcTypesGen.expectLong(executeGeneric(frame));
    }
}
