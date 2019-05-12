package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.BcLanguage;
import ch.snipy.bc.node.BcBinaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

public abstract class BcDivNode extends BcBinaryNode {

    @Specialization(guards = "isScaleZero()")
    protected long div(long left, long right) {
        return left / right;
    }

    @Specialization
    protected double div(double left, double right) {
        return left / right;
    }

    @Specialization
    protected BcBigNumber doBigNumber(BcBigNumber left, BcBigNumber right) {
        return left.divide(right);
    }

    protected boolean isScaleZero() {
        return BcLanguage.getCurrentContext().getScale() == 0;
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
