package ch.snipy.node.expression;

import ch.snipy.BcException;
import ch.snipy.node.BcUnaryNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "!")
public abstract class BcNotNode extends BcUnaryNode {

    @Specialization
    protected boolean not(boolean value) {
        return !value;
    }

    @Fallback
    protected Object typeError(Object value) {
        throw BcException.typeError(this, value);
    }
}
