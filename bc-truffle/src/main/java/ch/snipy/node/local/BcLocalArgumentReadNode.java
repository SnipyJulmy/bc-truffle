package ch.snipy.node.local;

import ch.snipy.node.BcExpressionNode;
import com.oracle.truffle.api.frame.VirtualFrame;

// Read a function's argument
// arguments are represented by an array of Object
public abstract class BcLocalArgumentReadNode extends BcExpressionNode {

    private final int index;

    public BcLocalArgumentReadNode(int index) {
        this.index = index;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return frame.getArguments()[index];
    }
}
