package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "%", description = "modulo operator")
public abstract class BcModNode extends BcBinaryNode {

    @Specialization
    protected boolean mod(boolean left, boolean right) {
        if (!right) throw new ArithmeticException("modulo by 0");
        return false;
    }

    @Specialization
    protected long mod(long left, long right) {
        return left % right;
    }

    @Specialization
    protected double mod(double left, double right) {
        return left % right;
    }

    @Specialization
    protected BcBigNumber mod(BcBigNumber left, BcBigNumber right) {
        return left.remainder(right);
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
