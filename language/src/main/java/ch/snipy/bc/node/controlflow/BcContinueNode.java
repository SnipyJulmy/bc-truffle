package ch.snipy.bc.node.controlflow;

import ch.snipy.bc.node.BcStatementNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "continue", description = "node implementing a continue statement")
public final class BcContinueNode extends BcStatementNode {

    @Override
    public void executeVoid(VirtualFrame frame) {
        throw BcContinueException.SINGLETON;
    }
}
