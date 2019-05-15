package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcUnaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import static ch.snipy.bc.runtime.BcBigNumber.FALSE;

@SuppressWarnings("WeakerAccess")
@NodeInfo(shortName = "!")
public abstract class BcNotNode extends BcUnaryNode {

    @Specialization
    protected long not(long value) {
        return value == 0 ? 1L : 0L;
    }

    @Specialization
    protected long not(BcBigNumber value) {
        return value.equals(FALSE) ? 1L : 0L;
    }

    @Fallback
    protected Object typeError(Object value) {
        throw BcException.typeError(this, value);
    }
}
