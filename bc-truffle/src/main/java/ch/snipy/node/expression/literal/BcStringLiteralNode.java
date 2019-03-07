package ch.snipy.node.expression.literal;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import ch.snipy.node.BcExpressionNode;

@NodeInfo(shortName = "const")
public class BcStringLiteralNode extends BcExpressionNode {
    private final String value;

    public BcStringLiteralNode(String value) {
        this.value = value;
    }



    @Override
    public String executeString(VirtualFrame frame) throws UnexpectedResultException {
        return value;
    }

    @Override
    public String executeGeneric(VirtualFrame frame) {
        return value;
    }
}
