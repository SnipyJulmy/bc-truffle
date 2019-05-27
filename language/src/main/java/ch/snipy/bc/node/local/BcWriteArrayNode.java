package ch.snipy.bc.node.local;

import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.node.BcReadNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;

import java.util.HashMap;
import java.util.Map;

@NodeChild(value = "index", type = BcExpressionNode.class)
@NodeChild(value = "expr", type = BcExpressionNode.class)
@NodeField(name = "slot", type = FrameSlot.class)
public abstract class BcWriteArrayNode extends BcReadNode {

    protected abstract FrameSlot getSlot();

    @Specialization
    public Object writeObject(VirtualFrame frame, BcBigNumber index, BcBigNumber expr) {
        Object res;
        if (contains(getGlobalFrame().getFrameDescriptor(), getSlot())) {
            res = getGlobalFrame().getValue(getSlot());
            if (res == null) {
                Map<Object, Object> map = new HashMap<>();
                getGlobalFrame().setObject(getSlot(), map);
                res = getGlobalFrame().getValue(getSlot());
            }
            assert res != null;
        } else {
            assert frame.getFrameDescriptor().getSlots().contains(getSlot());
            res = frame.getValue(getSlot());
            if (res == null) {
                Map<Object, Object> map = new HashMap<>();
                frame.setObject(getSlot(), map);
                res = frame.getValue(getSlot());
            }
            assert res != null;
        }
        Map<Object, Object> map = (Map<Object, Object>) res;
        map.put(index, expr);
        return expr;
    }

    @Specialization
    public Object writeObject(VirtualFrame frame, BcBigNumber index, BcExpressionNode expr) {
        Object res;
        if (getGlobalFrame().getFrameDescriptor().getSlots().contains(getSlot())) {
            res = getGlobalFrame().getValue(getSlot());
            if (res == null) {
                Map<Object, Object> map = new HashMap<>();
                getGlobalFrame().setObject(getSlot(), map);
                res = frame.getValue(getSlot());
            }
            assert res != null;
        } else {
            assert frame.getFrameDescriptor().getSlots().contains(getSlot());
            res = frame.getValue(getSlot());
            if (res == null) {
                Map<Object, Object> map = new HashMap<>();
                getGlobalFrame().setObject(getSlot(), map);
                res = frame.getValue(getSlot());
            }
            assert res != null;
        }
        Map<Object, Object> map = (Map<Object, Object>) res;
        map.put(index, expr);
        return expr;
    }

    @Specialization
    public Object writeObject(VirtualFrame frame, BcExpressionNode index, BcExpressionNode expr) {
        Object res;
        if (getGlobalFrame().getFrameDescriptor().getSlots().contains(getSlot())) {
            res = getGlobalFrame().getValue(getSlot());
            if (res == null) {
                Map<Object, Object> map = new HashMap<>();
                getGlobalFrame().setObject(getSlot(), map);
                res = frame.getValue(getSlot());
            }
            assert res != null;
        } else {
            assert frame.getFrameDescriptor().getSlots().contains(getSlot());
            res = frame.getValue(getSlot());
            if (res == null) {
                Map<Object, Object> map = new HashMap<>();
                getGlobalFrame().setObject(getSlot(), map);
                res = frame.getValue(getSlot());
            }
            assert res != null;
        }
        Map<Object, Object> map = (Map<Object, Object>) res;
        map.put(index, expr);
        return expr;
    }

    @TruffleBoundary
    private boolean contains(FrameDescriptor frameDescriptor, FrameSlot slot) {
        return frameDescriptor.getSlots().contains(slot);
    }

    private VirtualFrame getCorrectFrame(VirtualFrame localFrame) {
        if (isIn(localFrame.materialize())) return localFrame;
        else if (isIn(getGlobalFrame())) return getGlobalFrame();
        else return null;
    }

    private boolean isIn(MaterializedFrame frame) {
        return contains(frame.getFrameDescriptor(), getSlot());
    }
}
