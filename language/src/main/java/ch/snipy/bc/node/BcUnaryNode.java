package ch.snipy.bc.node;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;

/**
 * represent an abstract unary node for unary operator
 */
@NodeChildren({
        @NodeChild(value = "value", type = BcExpressionNode.class)
})
public abstract class BcUnaryNode extends BcExpressionNode {
}
