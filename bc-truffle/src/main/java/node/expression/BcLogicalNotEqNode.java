package node.expression;

import node.BcBinaryNode;
import org.graalvm.compiler.nodeinfo.NodeInfo;

@NodeInfo(shortName = "!=")
public abstract class BcLogicalNotEqNode extends BcBinaryNode {
}
