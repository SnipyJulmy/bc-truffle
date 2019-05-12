package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

public abstract class BcMulNode extends BcBinaryNode {

    @Specialization
    protected boolean mul(boolean left, boolean right) {
        return left && right;
    }

    @Specialization(rewriteOn = ArithmeticException.class)
    protected long mul(long left, long right) {
        return Math.multiplyExact(left, right);
    }

    @Specialization(rewriteOn = ArithmeticException.class)
    protected double mul(double left, double right) {
        double res = left * right;
        if (Double.isInfinite(res))
            if (Double.isInfinite(res))
                throw new ArithmeticException("mul result is infinite");
        return res;
    }

    @Specialization
    protected BcBigNumber mul(BcBigNumber left, BcBigNumber right) {
        return left.multiply(right);
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
