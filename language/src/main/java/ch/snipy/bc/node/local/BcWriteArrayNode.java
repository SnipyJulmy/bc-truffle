package ch.snipy.bc.node.local;

import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;

import java.util.HashMap;
import java.util.Map;

@NodeChild(value = "index", type = BcExpressionNode.class)
@NodeChild(value = "expr", type = BcExpressionNode.class)
@NodeField(name = "slot", type = FrameSlot.class)
@NodeField(name = "globalFrame", type = MaterializedFrame.class)
public abstract class BcWriteArrayNode extends BcExpressionNode {

    protected abstract FrameSlot getSlot();

    protected abstract MaterializedFrame getGlobalFrame();

    @Specialization
    public Object writeObject(VirtualFrame frame, BcBigNumber index, BcBigNumber expr) {
        Object mapValue = frame.getValue(getSlot());
        if (mapValue == null) {
            Map<Object, Object> map = new HashMap<>();
            frame.setObject(getSlot(), map);
            mapValue = frame.getValue(getSlot());
            assert mapValue != null;
        }
        Map<Object, Object> map = (Map<Object, Object>) mapValue;
        map.put(index, expr);
        return expr;
    }

    @Specialization
    public Object writeObject(VirtualFrame frame, BcBigNumber index, BcExpressionNode expr) {
        Object mapValue = frame.getValue(getSlot());
        if (mapValue == null) {
            Map<Object, Object> map = new HashMap<>();
            frame.setObject(getSlot(), map);
            mapValue = frame.getValue(getSlot());
            assert mapValue != null;
        }
        Map<Object, Object> map = (Map<Object, Object>) mapValue;
        map.put(index, expr);
        return expr;
    }

    @Specialization
    public Object writeObject(VirtualFrame frame, BcExpressionNode index, BcExpressionNode expr) {
        Object mapValue = frame.getValue(getSlot());
        if (mapValue == null) {
            Map<Object, Object> map = new HashMap<>();
            frame.setObject(getSlot(), map);
            mapValue = frame.getValue(getSlot());
            assert mapValue != null;
        }
        Object idx = index.executeGeneric(frame);
        Map<Object, Object> map = (Map<Object, Object>) mapValue;
        map.put(idx, expr);
        return expr;
    }
}
