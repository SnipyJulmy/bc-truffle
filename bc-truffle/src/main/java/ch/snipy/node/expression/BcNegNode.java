package ch.snipy.node.expression;

import ch.snipy.BcException;
import ch.snipy.node.BcUnaryNode;
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
