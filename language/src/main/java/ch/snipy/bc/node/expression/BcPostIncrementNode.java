package ch.snipy.bc.node.expression;

import ch.snipy.bc.node.BcReadNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

import static ch.snipy.bc.runtime.BcBigNumber.ONE;
import static ch.snipy.bc.runtime.BcBigNumber.ZERO;

@NodeField(name = "slot", type = FrameSlot.class)
@NodeField(name = "modifier", type = double.class)
public abstract class BcPostIncrementNode extends BcReadNode {

    protected abstract FrameSlot getSlot();

    protected abstract double getModifier();

    @SuppressWarnings("Duplicates")
    @Specialization
    public BcBigNumber doBcBigNumber(VirtualFrame localFrame) {
        BcBigNumber value;
        VirtualFrame frame;
        if (contains(localFrame.getFrameDescriptor(), getSlot())) {
            frame = localFrame;
            value = (BcBigNumber) FrameUtil.getObjectSafe(localFrame, getSlot());
        } else if (contains(getGlobalFrame().getFrameDescriptor(), getSlot())) {
            frame = getGlobalFrame();
            value = (BcBigNumber) FrameUtil.getObjectSafe(getGlobalFrame(), getSlot());
        } else {
            frame = localFrame;
            value = ZERO;
        }
        BcBigNumber newValue = value.add(getModifier() > 0.0 ? ONE : ONE.negate());
        frame.setObject(getSlot(), newValue);
        return value;
    }

    @TruffleBoundary
    private boolean contains(FrameDescriptor frameDescriptor, FrameSlot slot) {
        return frameDescriptor.getSlots().contains(slot);
    }
}
