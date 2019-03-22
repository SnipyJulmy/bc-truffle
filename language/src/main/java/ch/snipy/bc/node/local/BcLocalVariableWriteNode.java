package ch.snipy.bc.node.local;

import ch.snipy.bc.node.BcExpressionNode;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeChild(value = "expr", type = BcExpressionNode.class)
@NodeField(name = "slot", type = FrameSlot.class)
public abstract class BcLocalVariableWriteNode extends BcExpressionNode {

    protected abstract FrameSlot getSlot();

    // Generic method that write all possible type
    @Specialization
    public Object writeGeneric(VirtualFrame frame, Object exprValue) {
        frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Object);
        frame.setObject(getSlot(), exprValue);
        return exprValue;
    }
}
