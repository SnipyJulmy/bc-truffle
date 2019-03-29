package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import java.math.BigDecimal;

@NodeInfo(shortName = "<")
public abstract class BcLogicalLessThanNode extends BcBinaryNode {

    @Specialization
    protected BigDecimal lessThan(BigDecimal left, BigDecimal right) {
        int res = left.compareTo(right);
        switch (res) {
            case -1:
                return TRUE;
            case 0:
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
