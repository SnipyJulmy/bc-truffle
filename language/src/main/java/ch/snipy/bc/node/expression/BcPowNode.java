package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "^", description = "power operator")
public abstract class BcPowNode extends BcBinaryNode {

    @Specialization
    protected boolean pow(boolean left, boolean right) {
        if (!right) // right is 0
            if (!left) throw new ArithmeticException("0 to the power 0 is undefined");
            else return true;
        else
            return left;
    }

    @Specialization
    protected double pow(long left, long right) {
        double res = Math.pow(left, right);
        if (Double.isInfinite(res)) throw new ArithmeticException("power result is infinite");
        return res;
    }

    @Specialization
    protected double doDouble(double left, double right) {
        double res = Math.pow(left, right);
        if (Double.isInfinite(res)) throw new ArithmeticException("power result is infinite");
        return res;
    }

    @Specialization
    protected BcBigNumber pow(BcBigNumber left, BcBigNumber right) {
        return left.pow(right);
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
