package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "&&")
public abstract class BcAndNode extends BcBinaryNode {

    @Specialization
    protected boolean and(boolean left, boolean right) {
        return left && right;
    }

    @Specialization
    protected boolean and(long left, long right) {
        return left == 1 && right == 1;
    }

    @Specialization
    protected boolean and(double left, double right) {
        return left == 1.0 && right == 1.0;
    }

    @Specialization
    protected BcBigNumber doBigNumber(BcBigNumber left, BcBigNumber right) {
        return BcBigNumber.valueOf(left.booleanValue() && right.booleanValue());
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }

}
