package node;

import com.oracle.truffle.api.dsl.NodeChild;

@NodeChild("left")
@NodeChild("right")
public abstract class BcBinaryNode extends BcExpressionNode{

}
