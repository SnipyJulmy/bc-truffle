package ch.snipy.bc.node.local;

import ch.snipy.bc.node.BcExpressionNode;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

@NodeField(name = "slot", type = FrameSlot.class)
@NodeField(name = "globalFrame", type = MaterializedFrame.class)
public abstract class BcLocalVariableReadNode extends BcExpressionNode {

    protected abstract FrameSlot getSlot();

    protected abstract MaterializedFrame getGlobalFrame();

    @Specialization
    public Object readObject(VirtualFrame frame) {
        Object res;
        if(frame.getFrameDescriptor().getSlots().contains(getSlot()))
            res = frame.getValue(getSlot());
        else
            res = getGlobalFrame().getValue(getSlot());
        if (res == null) return 0;
        return res;
    }

    @ExplodeLoop
    private VirtualFrame getFrame(VirtualFrame frame) {
        while (!frame.getFrameDescriptor().getSlots().contains(getSlot())) {
            frame = (VirtualFrame) frame.getArguments()[0];
        }
        return frame;
    }
}
