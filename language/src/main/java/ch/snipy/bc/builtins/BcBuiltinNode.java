package ch.snipy.bc.builtins;

import ch.snipy.bc.BcException;
import ch.snipy.bc.BcLanguage;
import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.runtime.BCContext;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

@NodeChild(value = "arg", type = BcExpressionNode[].class)
public abstract class BcBuiltinNode extends BcExpressionNode {

    public final BCContext getContext() {
        return getRootNode().getLanguage(BcLanguage.class).getContextReference().get();
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        try {
            return execute(frame);
        } catch (UnsupportedSpecializationException e) {
            throw BcException.typeError(e.getNode(), e.getSuppliedValues());
        }
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        super.executeVoid(frame);
    }

    @Override
    public boolean executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
        return super.executeBoolean(frame);
    }

    @Override
    public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
        return super.executeDouble(frame);
    }

    protected abstract Object execute(VirtualFrame frame);
}
