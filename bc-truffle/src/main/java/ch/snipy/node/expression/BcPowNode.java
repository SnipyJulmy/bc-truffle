package ch.snipy.node.expression;

import ch.snipy.BcException;
import ch.snipy.node.BcBinaryNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "^", description = "power operator")
public abstract class BcPowNode extends BcBinaryNode {

    @Specialization
    protected double doDouble(double left, double right) {
        return Math.pow(left, right);
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
