package ch.snipy.bc.node.expression;

import ch.snipy.bc.node.BcReadNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.*;

import static ch.snipy.bc.runtime.BcBigNumber.ONE;

@SuppressWarnings("WeakerAccess")
@NodeField(name = "slot", type = FrameSlot.class)
@NodeField(name = "modifier", type = long.class)
public abstract class BcPostIncrementNode extends BcReadNode {

    protected abstract long getModifier();

    protected abstract FrameSlot getSlot();

    @Specialization
    public Object postIncrement(VirtualFrame localFrame) {
        VirtualFrame frame = getCorrectFrame(localFrame);
        if (frame == null) { // return 0 but set value to 1, since it is post incremented
            localFrame.setLong(getSlot(), 1);
            return 0;
        } else {
            try {
                long value = FrameUtil.getLongSafe(frame, getSlot());
                long newValue = value + getModifier();
                frame.setLong(getSlot(), newValue);
                return value;
            } catch (IllegalStateException eLong) { // long failed, we try with double
                try {
                    double value = FrameUtil.getDoubleSafe(frame, getSlot());
                    double newValue = value + getModifier();
                    frame.setDouble(getSlot(), newValue);
                    return value;
                } catch (IllegalStateException eDouble) { // double failed, go for BigNumber
                    BcBigNumber value = (BcBigNumber) FrameUtil.getObjectSafe(frame, getSlot());
                    BcBigNumber newValue = value.add(getModifier() > 0 ? ONE : ONE.negate());
                    frame.setObject(getSlot(), newValue);
                    return value;
                }
            }
        }

    }

    private VirtualFrame getCorrectFrame(VirtualFrame localFrame) {
        if (isIn(localFrame.materialize())) return localFrame;
        else if (isIn(getGlobalFrame().materialize())) return getGlobalFrame();
        else return null;
    }

    private boolean isIn(MaterializedFrame frame) {
        return contains(frame.getFrameDescriptor(), getSlot());
    }

    @TruffleBoundary
    private boolean contains(FrameDescriptor frameDescriptor, FrameSlot slot) {
        return frameDescriptor.getSlots().contains(slot);
    }
}
