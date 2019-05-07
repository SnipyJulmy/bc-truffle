package ch.snipy.bc.node.local;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcReadNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeField(name = "slot", type = FrameSlot.class)
public abstract class BcVariableReadNode extends BcReadNode {

    protected abstract FrameSlot getSlot();

    @Specialization
    public Object readObject(VirtualFrame localFrame) {
        if (getSlot() == null) throw BcException.typeError(this, "slot is null");
        Object res;
        if (getRootNode().getName().equals("main")) {
            if (contains(localFrame.getFrameDescriptor(), getSlot())) {
                res = localFrame.getValue(getSlot());
                if (res == null) {
                    localFrame.setObject(getSlot(), BcBigNumber.ZERO);
                    res = BcBigNumber.ZERO;
                }
            } else if (contains(getGlobalFrame().getFrameDescriptor(), getSlot())) {
                res = getGlobalFrame().getValue(getSlot());
                if (res == null) {
                    getGlobalFrame().setObject(getSlot(), BcBigNumber.ZERO);
                    res = BcBigNumber.ZERO;
                }
            } else {
                res = BcBigNumber.ZERO;
            }
        } else { // inside a function
            if (contains(localFrame.getFrameDescriptor(), getSlot())) {
                res = localFrame.getValue(getSlot());
                if (res == null) {
                    localFrame.setObject(getSlot(), BcBigNumber.ZERO);
                    res = BcBigNumber.ZERO;
                }
            } else if (contains(getGlobalFrame().getFrameDescriptor(), getSlot())) {
                res = getGlobalFrame().getValue(getSlot());
                if (res == null)
                    res = BcBigNumber.ZERO;
            } else {
                res = null;
                throw BcException.typeError(this, "unknow variable " + getSlot().getIdentifier());
            }
        }
        return res;
    }

    @TruffleBoundary
    private boolean contains(FrameDescriptor frameDescriptor, FrameSlot slot) {
        return frameDescriptor.getSlots().contains(slot);
    }
}
