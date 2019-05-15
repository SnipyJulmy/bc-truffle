package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcUnaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@SuppressWarnings("WeakerAccess")
@NodeInfo(shortName = "-")
public abstract class BcNegNode extends BcUnaryNode {

    @Specialization
    protected long negate(long value) {
        return -value;
    }

    @Specialization
    protected BcBigNumber negate(BcBigNumber value) {
        return BcBigNumber.valueOf(value.getValue().negate());
    }

    @Fallback
    protected Object typeError(Object value) {
        throw BcException.typeError(this, value);
    }
}
