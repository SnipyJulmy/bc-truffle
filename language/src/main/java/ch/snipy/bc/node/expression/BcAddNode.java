package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

import java.math.BigDecimal;

public abstract class BcAddNode extends BcBinaryNode {

    @Specialization
    protected BigDecimal doBigDecimal(BigDecimal left, BigDecimal right) {
        return left.add(right);
    }

    @Specialization(guards = "isString(left, right)")
    protected String doString(Object left, Object right) {
        return left.toString() + right.toString();
    }

    protected boolean isString(Object a, Object b) {
        return a instanceof String || b instanceof String;
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
