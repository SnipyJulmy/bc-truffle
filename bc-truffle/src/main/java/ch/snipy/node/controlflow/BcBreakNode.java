package ch.snipy.node.controlflow;

import ch.snipy.node.BcStatementNode;
import ch.snipy.node.controlflow.BcBreakException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "break", description = "node implementing a break statement")
public final class BcBreakNode extends BcStatementNode {

    @Override
    public void executeVoid(VirtualFrame frame) {
        throw BcBreakException.SINGLETON;
    }
}
