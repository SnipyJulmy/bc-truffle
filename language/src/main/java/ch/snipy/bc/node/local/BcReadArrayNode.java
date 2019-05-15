package ch.snipy.bc.node.local;

import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;

import java.util.Map;

@SuppressWarnings("WeakerAccess")
@NodeChild(value = "index", type = BcExpressionNode.class)
@NodeField(name = "slot", type = FrameSlot.class)
@NodeField(name = "globalFrame", type = MaterializedFrame.class)
public abstract class BcReadArrayNode extends BcExpressionNode {

    protected abstract FrameSlot getSlot();

    protected abstract MaterializedFrame getGlobalFrame();

    @Specialization
    protected Object readObject(VirtualFrame frame, BcBigNumber index) {
        Object res;
        if (contains(frame.getFrameDescriptor(), getSlot()))
            res = frame.getValue(getSlot());
        else
            res = getGlobalFrame().getValue(getSlot());
        if (res == null) return 0;
        //noinspection unchecked
        Map<Object, Object> map = (Map<Object, Object>) res;
        return map.getOrDefault(index, 0);
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
