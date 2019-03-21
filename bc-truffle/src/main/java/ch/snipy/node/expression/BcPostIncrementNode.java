package ch.snipy.node.expression;

import ch.snipy.node.BcExpressionNode;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeField(name = "slot", type = FrameSlot.class)
@NodeField(name = "modifier", type = double.class)
public abstract class BcPostIncrementNode extends BcExpressionNode {

    protected abstract FrameSlot getSlot();

    protected abstract double getModifier();

    @Specialization
    public double doDouble(VirtualFrame frame) {
        double value = FrameUtil.getDoubleSafe(frame, getSlot());
        double newValue = value + getModifier();
        frame.setDouble(getSlot(), newValue);
        return value;
    }
}
