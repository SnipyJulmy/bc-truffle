package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import java.math.BigDecimal;

@NodeInfo(shortName = "||")
public abstract class BcOrNode extends BcBinaryNode {

    @Specialization
    protected BigDecimal or(BigDecimal left, BigDecimal right) {
        return left.equals(BigDecimal.ZERO) && right.equals(BigDecimal.ZERO) ?
                BigDecimal.ZERO : BigDecimal.ONE;
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
