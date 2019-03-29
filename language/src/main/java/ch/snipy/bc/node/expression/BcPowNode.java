package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import ch.snipy.bc.node.BcBinaryNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import java.math.BigDecimal;

@NodeInfo(shortName = "^", description = "power operator")
public abstract class BcPowNode extends BcBinaryNode {

    @Specialization
    protected BigDecimal doDouble(BigDecimal left, BigDecimal right) {
        int exponent = right.intValue(); // fixme is this correct ?
        return left.pow(exponent);
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
