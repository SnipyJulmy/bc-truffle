package ch.snipy.bc.builtins;

import ch.snipy.bc.BcException;
import ch.snipy.bc.BcLanguage;
import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.runtime.BCContext;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

@NodeChild(value = "args", type = BcExpressionNode[].class)
@GenerateNodeFactory
public abstract class BcBuiltinNode extends BcExpressionNode {

    final BCContext getContext() {
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
    public void executeVoid(VirtualFrame frame) {
        super.executeVoid(frame);
    }

    @Override
    public BcBigNumber executeBigNumber(VirtualFrame frame) throws UnexpectedResultException {
        return super.executeBigNumber(frame);
    }

    @Override
    public String executeString(VirtualFrame frame) throws UnexpectedResultException {
        return super.executeString(frame);
    }

    @Override
    public Object[] executeObjectArray(VirtualFrame frame) throws UnexpectedResultException {
        return super.executeObjectArray(frame);
    }

    protected abstract Object execute(VirtualFrame frame);
}
