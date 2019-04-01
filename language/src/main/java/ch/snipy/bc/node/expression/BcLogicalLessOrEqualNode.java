package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import static ch.snipy.bc.runtime.BcBigNumber.FALSE;
import static ch.snipy.bc.runtime.BcBigNumber.TRUE;

@NodeInfo(shortName = "<=")
public abstract class BcLogicalLessOrEqualNode extends BcBinaryNode {

    @Specialization
    protected BcBigNumber lessOrEqual(BcBigNumber left, BcBigNumber right) {
        int res = left.compareTo(right);
        switch (res) {
            case 0:
            case -1:
                return TRUE;
            case 1:
                return FALSE;
        }
        assert false; // should never happen
        return null;
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
