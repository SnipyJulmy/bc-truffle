package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

import java.math.BigDecimal;

public abstract class BcDivNode extends BcBinaryNode {

    @Specialization
    protected BigDecimal doBigDecimal(BigDecimal left, BigDecimal right) {
        return left.divide(right, BigDecimal.ROUND_FLOOR); // fixme check posix
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
