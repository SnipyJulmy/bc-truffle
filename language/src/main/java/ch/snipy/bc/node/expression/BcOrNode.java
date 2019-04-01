package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import static ch.snipy.bc.runtime.BcBigNumber.valueOf;

@NodeInfo(shortName = "||")
public abstract class BcOrNode extends BcBinaryNode {

    @Specialization
    protected BcBigNumber or(BcBigNumber left, BcBigNumber right) {
        return valueOf(left.asBoolean() || right.asBoolean());
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
