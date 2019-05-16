package ch.snipy.bc.node.expression.literal;

import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

/**
 *  Constant literal for a Big decimal number
 */
@NodeInfo(shortName = "const")
public final class BcBigNumberLiteralNode extends BcExpressionNode {

    private final BcBigNumber value;

    public BcBigNumberLiteralNode(BcBigNumber value) {
        this.value = value;
    }

    @Override
    public BcBigNumber executeGeneric(VirtualFrame frame) {
        return value;
    }
}
