package ch.snipy.bc.node.local;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcReadNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

@NodeField(name = "slot", type = FrameSlot.class)
public abstract class BcVariableReadNode extends BcReadNode {

    protected abstract FrameSlot getSlot();

    @Override
    public boolean executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
        Object res = read(frame);
        if (res instanceof Boolean) {
            return ((Boolean) res);
        } else if (res instanceof Long) {
            return ((Long) res) != 0;
        } else if (res instanceof Double) {
            return ((Double) res) != 0;
        } else if (res instanceof BcBigNumber) {
            return ((BcBigNumber) res).booleanValue();
        } else {
            return super.executeBoolean(frame);
        }
    }

    @Specialization
    public Object read(VirtualFrame localFrame) {
        if (getSlot() == null) throw BcException.typeError(this, "slot is null");
        Object res;
        VirtualFrame frame;
        if (getRootNode().getName().equals("main")) {
            frame = getCorrectFrame(localFrame);
            if (frame == null) {
                throw BcException.typeError(this, "unknown variable " + getSlot().getIdentifier());
            }
            res = frame.getValue(getSlot());
            if (res == null) {
                frame.setObject(getSlot(), BcBigNumber.ZERO);
                return BcBigNumber.ZERO;
            }
            return res;
        } else { // inside a function
            frame = getCorrectFrame(localFrame);
            if (frame == null) {
                throw BcException.typeError(this, "unknown variable " + getSlot().getIdentifier());
            }
            res = localFrame.getValue(getSlot());
            if (res == null) return BcBigNumber.ZERO;
            return res;
        }
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
