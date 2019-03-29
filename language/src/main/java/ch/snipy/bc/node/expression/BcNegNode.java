package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcUnaryNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

import java.math.BigDecimal;

public abstract class BcNegNode extends BcUnaryNode {

    @Specialization
    protected BigDecimal doDouble(BigDecimal value) {
        return value.negate();
    }


    @Fallback
    protected Object typeError(Object value) {
        throw BcException.typeError(this, value);
    }
}
