package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

/**
 * Node which perform the + operator on various type
 * Comparing to posix bc, here we allow String concatenation
 */
@SuppressWarnings("WeakerAccess")
@NodeInfo(shortName = "+")
public abstract class BcAddNode extends BcBinaryNode {

    @Specialization(rewriteOn = ArithmeticException.class)
    protected long add(long left, long right) {
        return Math.addExact(left, right);
    }

    @Specialization
    @TruffleBoundary
    protected BcBigNumber add(BcBigNumber left, BcBigNumber right) {
        return BcBigNumber.valueOf(left.getValue().add(right.getValue()));
    }

    @Specialization(guards = "isString(left, right)")
    @TruffleBoundary
    protected String add(Object left, Object right) {
        return left.toString() + right.toString();
    }

    protected boolean isString(Object a, Object b) {
        return a instanceof String || b instanceof String;
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
