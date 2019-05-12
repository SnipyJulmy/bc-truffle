package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "+")
public abstract class BcAddNode extends BcBinaryNode {

    @Specialization(rewriteOn = ArithmeticException.class)
    protected long add(long left, long right) {
        return Math.addExact(left, right);
    }

    @Specialization(rewriteOn = ArithmeticException.class)
    protected double add(double left, double right) {
        double res = left + right;
        if (Double.isInfinite(res))
            throw new ArithmeticException("add result is infinite");
        return res;
    }

    @Specialization
    protected BcBigNumber add(BcBigNumber left, BcBigNumber right) {
        return left.add(right);
    }

    @Specialization(guards = "isString(left, right)")
    protected String add(Object left, Object right) {
        return left.toString() + right.toString();
    }

    protected boolean isString(Object a, Object b) {
        return a instanceof String || b instanceof String;
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
