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
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import static ch.snipy.bc.runtime.BcBigNumber.ONE;

@SuppressWarnings("WeakerAccess")
@NodeField(name = "slot", type = FrameSlot.class)
@NodeField(name = "modifier", type = long.class)
public abstract class BcPreIncrementNode extends BcReadNode {

    protected abstract long getModifier();

    protected abstract FrameSlot getSlot();

    @Specialization
    protected Object preIncrement(VirtualFrame localFrame) {
        VirtualFrame frame = getCorrectFrame(localFrame);
        if (frame == null) { // return 1 since 0 (default value) + 1 is 1
            localFrame.setLong(getSlot(), 1);
            return 1;
        } else {
            try {
                long value = FrameUtil.getLongSafe(frame, getSlot());
                long newValue = value + getModifier();
                frame.setLong(getSlot(), newValue);
                return newValue;
            } catch (IllegalStateException eLong) { // long failed, we try with double
                try {
                    double value = FrameUtil.getDoubleSafe(frame, getSlot());
                    double newValue = value + getModifier();
                    frame.setDouble(getSlot(), newValue);
                    return newValue;
                } catch (IllegalStateException eDouble) { // double failed, go for BigNumber
                    BcBigNumber value = (BcBigNumber) FrameUtil.getObjectSafe(frame, getSlot());
                    BcBigNumber newValue = value.add(getModifier() > 0 ? ONE : ONE.negate());
                    frame.setObject(getSlot(), newValue);
                    return newValue;
                }
            }
        }
    }

    private VirtualFrame getCorrectFrame(VirtualFrame localFrame) {
        if (isIn(localFrame)) return localFrame;
        else if (isIn(getGlobalFrame())) return getGlobalFrame();
        else return null;
    }

    private boolean isIn(VirtualFrame frame) {
        return contains(frame.getFrameDescriptor(), getSlot());
    }

    @TruffleBoundary
    private boolean contains(FrameDescriptor frameDescriptor, FrameSlot slot) {
        return frameDescriptor.getSlots().contains(slot);
    }
}
