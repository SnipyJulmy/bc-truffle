package ch.snipy.node.controlflow;

import ch.snipy.node.BcStatementNode;
import ch.snipy.node.controlflow.BcHaltException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "halt", description = "node representing the halt of the program")
public final class BcHaltNode extends BcStatementNode {
    @Override
    public void executeVoid(VirtualFrame frame) {
        throw BcHaltException.SINGLETON;
    }
}
