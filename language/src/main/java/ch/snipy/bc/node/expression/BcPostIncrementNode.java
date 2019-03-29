package ch.snipy.bc.node.expression;

import ch.snipy.bc.node.BcExpressionNode;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

import java.math.BigDecimal;

@NodeField(name = "slot", type = FrameSlot.class)
@NodeField(name = "modifier", type = double.class)
public abstract class BcPostIncrementNode extends BcExpressionNode {

    protected abstract FrameSlot getSlot();

    protected abstract double getModifier();

    @SuppressWarnings("Duplicates")
    @Specialization
    public BigDecimal doBigDecimal(VirtualFrame frame) {
        BigDecimal value = (BigDecimal) FrameUtil.getObjectSafe(frame, getSlot());
        BigDecimal newValue = value.add(getModifier() > 0.0 ? BigDecimal.ONE : BigDecimal.ONE.negate());
        frame.setObject(getSlot(), newValue);
        return value;
    }
}
