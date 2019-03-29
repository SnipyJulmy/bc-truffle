package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import java.math.BigDecimal;

@NodeInfo(shortName = "%", description = "modulo operator")
public abstract class BcModNode extends BcBinaryNode {

    @Specialization
    protected BigDecimal doDouble(BigDecimal left, BigDecimal right) {
        return left.remainder(right);
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
