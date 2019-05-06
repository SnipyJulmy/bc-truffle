package ch.snipy.bc.node.local;

import ch.snipy.bc.node.BcReadNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeField(name = "slot", type = FrameSlot.class)
public abstract class BcLocalVariableReadNode extends BcReadNode {

    protected abstract FrameSlot getSlot();

    @Specialization
    public Object readObject(VirtualFrame frame) {
        Object res;
        if (frame.getFrameDescriptor().getSlots().contains(getSlot()))
            res = frame.getValue(getSlot());
        else
            res = getGlobalFrame().getValue(getSlot());
        if (res == null) return BcBigNumber.valueOf(0);
        return res;
    }
}
