package ch.snipy.bc.node.local;

import ch.snipy.bc.node.BcExpressionNode;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeField(name = "slot", type = FrameSlot.class)
public abstract class BcLocalVariableReadNode extends BcExpressionNode {

    protected abstract FrameSlot getSlot();

    @Specialization
    public Object readObject(VirtualFrame frame) {
        Object res = frame.getValue(getSlot());
        if (res == null) return 0;
        return res;
    }
}
