package ch.snipy.bc.node;

import com.oracle.truffle.api.dsl.ReportPolymorphism;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(language = "bc", description = "abstract base node for any bc statement")
@ReportPolymorphism
public abstract class BcStatementNode extends Node {
    // execute the node as a statement (no return value)
    public abstract void executeVoid(VirtualFrame frame);
}
