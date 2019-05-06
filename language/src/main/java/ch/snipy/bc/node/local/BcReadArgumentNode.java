package ch.snipy.bc.node.local;

import ch.snipy.bc.node.BcExpressionNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.BranchProfile;

import static ch.snipy.bc.runtime.BcBigNumber.ZERO;

public class BcReadArgumentNode extends BcExpressionNode {

    private final int index;
    private final BranchProfile outOfBoundsTaken = BranchProfile.create();

    public BcReadArgumentNode(int index) {
        this.index = index;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (index < args.length)
            return args[index];
        else {
            outOfBoundsTaken.enter();
            return ZERO;
        }
    }
}
