package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "==")
public abstract class BcLogicalEqNode extends BcBinaryNode {

    @Specialization
    protected BcBigNumber equal(BcBigNumber left, BcBigNumber right) {
        return left.equals(right) ? BcBigNumber.TRUE : BcBigNumber.FALSE;
    }

    @Specialization
    protected BcBigNumber equal(String left, String right) {
        return left.equals(right) ? BcBigNumber.TRUE : BcBigNumber.FALSE;
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
