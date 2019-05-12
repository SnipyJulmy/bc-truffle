package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "<=")
public abstract class BcLogicalLessOrEqualNode extends BcBinaryNode {

    /*
            --> that's logical implication
            +-------+--------+-----+
            |  left |  right | leq |
            +-------+--------+-----+
            |   0   |   0    |  1  |
            +-------+--------+-----+
            |   0   |   1    |  1  |
            +-------+--------+-----+
            |   1   |   0    |  0  |
            +-------+--------+-----+
            |   1   |   1    |  1  |
            +-------+--------+-----+
     */
    @Specialization
    protected boolean lessOrEqual(boolean left, boolean right) {
        return !left || right;
    }

    @Specialization
    protected boolean lessOrEqual(long left, long right) {
        return left <= right;
    }

    @Specialization
    protected boolean lessOrEqual(double left, double right) {
        return left <= right;
    }

    @Specialization
    protected boolean lessOrEqual(BcBigNumber left, BcBigNumber right) {
        int res = left.compareTo(right);
        return (res < 0 || res == 0);
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
