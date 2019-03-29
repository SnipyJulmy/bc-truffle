package ch.snipy.bc.node.expression.literal;

import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.node.BcExpressionNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import java.math.BigDecimal;

@NodeInfo(shortName = "const")
public class BcDoubleLiteralNode extends BcExpressionNode {
    private final BigDecimal value;

    public BcDoubleLiteralNode(BigDecimal value) {
        this.value = value;
    }

    @Override
    public BigDecimal executeBigDecimal(VirtualFrame frame) throws UnexpectedResultException {
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
