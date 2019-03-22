package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcUnaryNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

public abstract class BcNegNode extends BcUnaryNode {

    @Specialization
    protected double doDouble(double value) {
        return -value;
    }


    @Fallback
    protected Object typeError(Object value) {
        throw BcException.typeError(this, value);
    }
}
