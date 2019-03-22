package ch.snipy.bc.node;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;

@NodeChildren({
        @NodeChild(value = "left", type = BcExpressionNode.class),
        @NodeChild(value = "right", type = BcExpressionNode.class)
})
public abstract class BcBinaryNode extends BcExpressionNode {

}
