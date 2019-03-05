package node.expression;

import node.BcUnaryNode;
import org.graalvm.compiler.nodeinfo.NodeInfo;

@NodeInfo(shortName = "!")
public abstract class BcNotNode extends BcUnaryNode {
}
