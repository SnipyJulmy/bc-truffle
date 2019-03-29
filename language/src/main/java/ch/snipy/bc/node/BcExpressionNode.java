package ch.snipy.bc.node;


import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import java.math.BigDecimal;

@NodeInfo(description = "Base class for any expression ch.snipy.node")
@TypeSystemReference(BcTypes.class)
public abstract class BcExpressionNode extends BcStatementNode {

    public static final BigDecimal FALSE = BigDecimal.ZERO;
    public static final BigDecimal TRUE = BigDecimal.ONE;

    // execute this when no specialization is possible --> most possible generalization
    public abstract Object executeGeneric(VirtualFrame frame);

    // the return value is not needed
    @Override
    public void executeVoid(VirtualFrame frame) {
        executeGeneric(frame);
    }

    public BigDecimal executeBigDecimal(VirtualFrame frame) throws UnexpectedResultException {
        return BcTypesGen.expectBigDecimal(executeGeneric(frame));
    }

    public String executeString(VirtualFrame frame) throws UnexpectedResultException {
        return BcTypesGen.expectString(executeGeneric(frame));
    }

    public Object[] executeObjectArray(VirtualFrame frame) throws UnexpectedResultException {
        return BcTypesGen.expectObjectArray(executeGeneric(frame));
    }
}
