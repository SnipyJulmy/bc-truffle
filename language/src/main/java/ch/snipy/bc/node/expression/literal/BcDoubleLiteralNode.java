package ch.snipy.bc.node.expression.literal;

import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import java.math.BigDecimal;

@NodeInfo(shortName = "const")
public class BcDoubleLiteralNode extends BcExpressionNode {
    private final BcBigNumber value;

    public BcDoubleLiteralNode(BcBigNumber value) {
        this.value = value;
    }

    @Override
    public BcBigNumber executeBigNumber(VirtualFrame frame) throws UnexpectedResultException {
        return value;
    }

    @Override
    public String executeString(VirtualFrame frame) throws UnexpectedResultException {
        return "" + value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }
}
