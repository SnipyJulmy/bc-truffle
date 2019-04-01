package ch.snipy.bc.node;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;

@NodeChildren({
        @NodeChild(value = "getValue", type = BcExpressionNode.class)
})
public abstract class BcUnaryNode extends BcExpressionNode {
}
