package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcUnaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

public abstract class BcNegNode extends BcUnaryNode {

    @Specialization
    protected BcBigNumber doDouble(BcBigNumber value) {
        return value.negate();
    }


    @Fallback
    protected Object typeError(Object value) {
        throw BcException.typeError(this, value);
    }
}
