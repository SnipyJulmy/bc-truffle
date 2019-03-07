package ch.snipy.node.expression;

import ch.snipy.BcException;
import ch.snipy.node.BcBinaryNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "!=")
public abstract class BcLogicalNotEqNode extends BcBinaryNode {

    @Specialization
    protected boolean notEqual(double left, double right) {
        return left == right;
    }

    @Specialization
    protected boolean notEqual(boolean left, boolean right) {
        return left == right;
    }

    @Specialization
    protected boolean notEqual(String left, String right) {
        return left.equals(right);
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
