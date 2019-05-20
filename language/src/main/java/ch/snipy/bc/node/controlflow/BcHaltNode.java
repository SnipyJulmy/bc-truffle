package ch.snipy.bc.node.controlflow;

import ch.snipy.bc.node.BcStatementNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "halt", description = "node representing the halt of the program")
public final class BcHaltNode extends BcStatementNode {
    @Override
    public void executeVoid(VirtualFrame frame) {
        // Do nothing for the moment
        // System.exit(0); // fixme : is here the right place to do it ?
        // TODO -> change halt behavior
        // throw BcHaltException.SINGLETON;
    }
}
