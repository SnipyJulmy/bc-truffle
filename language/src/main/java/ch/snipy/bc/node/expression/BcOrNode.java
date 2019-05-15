package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@SuppressWarnings("WeakerAccess")
@NodeInfo(shortName = "||")
public abstract class BcOrNode extends BcBinaryNode {

    @Specialization
    protected long or(long left, long right) {
        return left != 0 || right != 0 ? 1L : 0L;
    }

    @Specialization
    protected long or(BcBigNumber left, BcBigNumber right) {
        return left.booleanValue() || right.booleanValue() ? 1L : 0L;
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
