package ch.snipy.bc.node.expression;

import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeField(name = "slot", type = FrameSlot.class)
@NodeField(name = "modifier", type = double.class)
public abstract class BcPreIncrementNode extends BcExpressionNode {

    protected abstract FrameSlot getSlot();

    protected abstract double getModifier();

    @SuppressWarnings("Duplicates")
    @Specialization
    public BcBigNumber doBcBigNumber(VirtualFrame frame) {
        BcBigNumber value = (BcBigNumber) FrameUtil.getObjectSafe(frame, getSlot());
        BcBigNumber newValue = value.add(getModifier() > 0.0 ? BcBigNumber.ONE : BcBigNumber.ONE.negate());
        frame.setObject(getSlot(), newValue);
        return newValue;
    }
}
