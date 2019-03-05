package node;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;

@NodeChildren({
        @NodeChild(value = "value", type = BcExpressionNode.class)
})
public abstract class BcUnaryNode extends BcExpressionNode {
}
