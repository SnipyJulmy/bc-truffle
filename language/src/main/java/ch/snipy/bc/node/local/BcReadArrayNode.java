package ch.snipy.bc.node.local;

import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;

import java.util.Map;

@NodeChild(value = "index", type = BcExpressionNode.class)
@NodeField(name = "slot", type = FrameSlot.class)
@NodeField(name = "globalFrame", type = MaterializedFrame.class)
public abstract class BcReadArrayNode extends BcExpressionNode {

    protected abstract FrameSlot getSlot();

    protected abstract MaterializedFrame getGlobalFrame();

    @Specialization
    public Object readObject(VirtualFrame frame, BcBigNumber index) {
        Object mapValue = frame.getValue(getSlot());
        if (mapValue == null) return 0;
        Map<Object, Object> map = (Map<Object, Object>) mapValue;
        return map.getOrDefault(index, 0);
    }

    @Specialization
    public Object readObject(VirtualFrame frame, BcExpressionNode index) {
        Object mapValue = frame.getValue(getSlot());
        if (mapValue == null) return 0;
        Object idx = (BcBigNumber) index.executeGeneric(frame);
        Map<Object, Object> map = (Map<Object, Object>) mapValue;
        return map.getOrDefault(idx, 0);
    }
}
