package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@SuppressWarnings("WeakerAccess")
@NodeInfo(shortName = "&&")
public abstract class BcAndNode extends BcBinaryNode {

    protected long and(long left, long right) {
        return left != 0 && right != 0 ? 1L : 0L;
    }

    @Specialization
    @TruffleBoundary
    protected BcBigNumber and(BcBigNumber left, BcBigNumber right) {
        return BcBigNumber.valueOf(left.booleanValue() && right.booleanValue());
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }

}
