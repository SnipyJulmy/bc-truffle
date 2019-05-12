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
    protected boolean equal(long left, long right) {
        return left == right;
    }

    @Specialization
    protected boolean equal(double left, double right) {
        return left == right;
    }

    @Specialization
    protected boolean equal(BcBigNumber left, BcBigNumber right) {
        return left.equals(right);
    }

    @Specialization
    protected boolean equal(String left, String right) {
        return left.equals(right);
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
