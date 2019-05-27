package ch.snipy.bc.node.local;

import ch.snipy.bc.BcLanguage;
import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.*;

@NodeChild(value = "expr", type = BcExpressionNode.class)
@NodeField(name = "slot", type = FrameSlot.class)
@NodeField(name = "globalFrame", type = MaterializedFrame.class)
public abstract class BcVariableWriteNode extends BcExpressionNode {

    protected abstract FrameSlot getSlot();

    protected abstract MaterializedFrame getGlobalFrame();

    @Specialization
    long write(VirtualFrame localFrame, long value) {
        if (getSlot().getIdentifier().equals("scale")) {
            BcLanguage.getCurrentContext().setScale((int) value);
        }
        VirtualFrame frame = getCorrectFrame(localFrame);
        if (frame == null) {
            createFrameSlot(localFrame.getFrameDescriptor(), getSlot());
            localFrame.setLong(getSlot(), value);
        } else {
            frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Long);
            frame.setLong(getSlot(), value);
        }
        return value;
    }

    @Specialization
    public BcBigNumber write(VirtualFrame localFrame, BcBigNumber value) {
        if (getSlot().getIdentifier().equals("scale")) {
            BcLanguage.getCurrentContext().setScale(value.intValue());
        }
        VirtualFrame frame = getCorrectFrame(localFrame);
        if (frame == null) {
            createFrameSlot(localFrame.getFrameDescriptor(), getSlot());
            localFrame.setObject(getSlot(), value);
        } else {
            frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Object);
            frame.setObject(getSlot(), value);
        }
        return value;
    }

    // Generic method that write all possible type
    @Specialization
    public Object writeGeneric(VirtualFrame frame, Object exprValue) {
        if (contains(frame.getFrameDescriptor(), getSlot())) {
            frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Object);
            frame.setObject(getSlot(), exprValue);
        } else {
            getGlobalFrame().getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Object);
            getGlobalFrame().setObject(getSlot(), exprValue);
        }
        return exprValue;
    }

    @TruffleBoundary
    private boolean contains(FrameDescriptor frameDescriptor, FrameSlot slot) {
        return frameDescriptor.getSlots().contains(slot);
    }

    @TruffleBoundary
    private void createFrameSlot(FrameDescriptor frameDescriptor, FrameSlot slot) {
        frameDescriptor.findOrAddFrameSlot(slot.getIdentifier(), FrameSlotKind.Object);
    }

    private VirtualFrame getCorrectFrame(VirtualFrame localFrame) {
        if (isIn(localFrame.materialize(), getSlot())) return localFrame;
        else if (isIn(getGlobalFrame(), getSlot())) return getGlobalFrame();
        else return null;
    }

    private boolean isIn(MaterializedFrame frame, FrameSlot slot) {
        return contains(frame.getFrameDescriptor(), slot);
    }
}
