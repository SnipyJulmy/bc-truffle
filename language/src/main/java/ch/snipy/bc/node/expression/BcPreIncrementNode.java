package ch.snipy.bc.node.expression;

import ch.snipy.bc.node.BcExpressionNode;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

@NodeField(name = "slot", type = FrameSlot.class)
@NodeField(name = "modifier", type = double.class)
public abstract class BcPreIncrementNode extends BcExpressionNode {

    protected abstract FrameSlot getSlot();

    protected abstract double getModifier();

    @Specialization
    public double doDouble(VirtualFrame frame) {
        double value = FrameUtil.getDoubleSafe(frame, getSlot());
        double newValue = value + getModifier();
        frame.setDouble(getSlot(), newValue);
        return newValue;
    }
}
