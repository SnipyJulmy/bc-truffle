package ch.snipy.bc.builtins;

import ch.snipy.bc.BcException;
import ch.snipy.bc.BcLanguage;
import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.runtime.BcContext;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

/**
 * Define the base class for the bc builtins
 * The builtin functions are registered in the {@link BcContext} class
 */
@NodeChild(value = "args", type = BcExpressionNode[].class)
@GenerateNodeFactory
public abstract class BcBuiltinNode extends BcExpressionNode {

    final BcContext getContext() {
        return BcLanguage.getCurrentContext();
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
    public long executeLong(VirtualFrame frame) throws UnexpectedResultException {
        return super.executeLong(frame);
    }

    @Override
    public String executeString(VirtualFrame frame) throws UnexpectedResultException {
        return super.executeString(frame);
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        super.executeVoid(frame);
    }

    protected abstract Object execute(VirtualFrame frame);
}
