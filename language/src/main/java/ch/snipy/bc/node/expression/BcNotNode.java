package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcUnaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import static ch.snipy.bc.runtime.BcBigNumber.FALSE;

@NodeInfo(shortName = "!")
public abstract class BcNotNode extends BcUnaryNode {

    @Specialization
    protected boolean not(boolean value) {
        return !value;
    }

    @Specialization
    protected boolean not(long value) {
        return value == 0;
    }

    @Specialization
    protected boolean not(double value) {
        return value == 0.0;
    }

    @Specialization
    protected boolean not(BcBigNumber value) {
        return value.equals(FALSE);
    }

    @Fallback
    protected Object typeError(Object value) {
        throw BcException.typeError(this, value);
    }
}
