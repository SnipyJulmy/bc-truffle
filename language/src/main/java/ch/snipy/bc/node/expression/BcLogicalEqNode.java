package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import java.math.BigDecimal;

@NodeInfo(shortName = "==")
public abstract class BcLogicalEqNode extends BcBinaryNode {

    @Specialization
    protected BigDecimal equal(BigDecimal left, BigDecimal right) {
        return left.equals(right) ? BigDecimal.ONE : BigDecimal.ZERO;
    }

    @Specialization
    protected BigDecimal equal(String left, String right) {
        return left.equals(right) ? BigDecimal.ONE : BigDecimal.ZERO;
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
