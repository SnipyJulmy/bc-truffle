package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "||")
public abstract class BcOrNode extends BcBinaryNode {

    @Specialization
    protected boolean or(boolean left, boolean right) {
        return left || right;
    }

    @Specialization
    protected boolean or(long left, long right) {
        return (left != 0) || (right != 0);
    }

    @Specialization
    protected boolean or(double left, double right) {
        return (left != 0) || (right != 0);
    }

    @Specialization
    protected boolean or(BcBigNumber left, BcBigNumber right) {
        return left.booleanValue() || right.booleanValue();
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
