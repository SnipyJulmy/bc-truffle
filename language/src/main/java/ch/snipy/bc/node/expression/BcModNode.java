package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@SuppressWarnings("WeakerAccess")
@NodeInfo(shortName = "%", description = "modulo operator")
public abstract class BcModNode extends BcBinaryNode {

    @Specialization
    protected long mod(long left, long right) {
        return left % right;
    }

    @Specialization
    @TruffleBoundary
    protected BcBigNumber mod(BcBigNumber left, BcBigNumber right) {
        return BcBigNumber.valueOf(left.getValue().remainder(right.getValue()));
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
