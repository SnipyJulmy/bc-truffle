package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.BcLanguage;
import ch.snipy.bc.node.BcBinaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

@SuppressWarnings("WeakerAccess")
public abstract class BcDivNode extends BcBinaryNode {

    @Specialization(guards = "isScaleZero()")
    protected long div(long left, long right) {
        return left / right;
    }

    @Specialization
    @TruffleBoundary
    protected BcBigNumber doBigNumber(BcBigNumber left, BcBigNumber right) {
        return BcBigNumber.valueOf(
                left.getValue().divide(
                        right.getValue(),
                        BcLanguage.getCurrentContext().getScale(),
                        BcLanguage.getCurrentContext().getRoundingMode()
                ));
    }

    protected boolean isScaleZero() {
        return BcLanguage.getCurrentContext().getScale() == 0;
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
