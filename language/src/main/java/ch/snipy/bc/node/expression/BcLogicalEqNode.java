package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@SuppressWarnings("WeakerAccess")
@NodeInfo(shortName = "==")
public abstract class BcLogicalEqNode extends BcBinaryNode {

    @Specialization
    protected long equal(long left, long right) {
        return left == right ? 1L : 0L;
    }

    @Specialization
    @TruffleBoundary
    protected long equal(BcBigNumber left, BcBigNumber right) {
        return left.equals(right) ? 1L : 0L;
    }

    @Specialization
    protected long equal(String left, String right) {
        return left.equals(right) ? 1L : 0L;
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
