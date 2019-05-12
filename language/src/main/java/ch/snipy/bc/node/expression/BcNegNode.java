package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcUnaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

public abstract class BcNegNode extends BcUnaryNode {

    @Specialization
    protected boolean negate(boolean value) {
        return !value;
    }

    @Specialization
    protected long negate(long value) {
        return -value;
    }

    @Specialization
    protected double negate(double value) {
        return -value;
    }

    @Specialization
    protected BcBigNumber negate(BcBigNumber value) {
        return value.negate();
    }


    @Fallback
    protected Object typeError(Object value) {
        throw BcException.typeError(this, value);
    }
}
