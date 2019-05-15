package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@SuppressWarnings("WeakerAccess")
@NodeInfo(shortName = "<=")
public abstract class BcLogicalLessOrEqualNode extends BcBinaryNode {

    @Specialization
    protected long lessOrEqual(long left, long right) {
        return left <= right ? 1L : 0L;
    }

    @Specialization
    @TruffleBoundary
    protected long lessOrEqual(BcBigNumber left, BcBigNumber right) {
        int res = left.compareTo(right);
        return (res < 0 || res == 0) ? 1L : 0L;
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
