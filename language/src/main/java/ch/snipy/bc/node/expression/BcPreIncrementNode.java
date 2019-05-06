package ch.snipy.bc.node.expression;

import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

import static ch.snipy.bc.runtime.BcBigNumber.ONE;
import static ch.snipy.bc.runtime.BcBigNumber.ZERO;

@NodeField(name = "slot", type = FrameSlot.class)
@NodeField(name = "modifier", type = double.class)
public abstract class BcPreIncrementNode extends BcExpressionNode {

    protected abstract FrameSlot getSlot();

    protected abstract double getModifier();

    // TODO search in the upper context
    @SuppressWarnings("Duplicates")
    @Specialization
    public BcBigNumber doBcBigNumber(VirtualFrame frame) {
        BcBigNumber value = (BcBigNumber) FrameUtil.getObjectSafe(frame, getSlot());
        if (value == null) {
            value = ZERO;
        }
        BcBigNumber newValue = value.add(getModifier() > 0.0 ? ONE : ONE.negate());
        frame.setObject(getSlot(), newValue);
        return newValue;
    }
}
