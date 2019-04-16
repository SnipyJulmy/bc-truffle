package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcException;
import ch.snipy.bc.node.BcBinaryNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "^", description = "power operator")
public abstract class BcPowNode extends BcBinaryNode {

    @Specialization
    protected BcBigNumber doDouble(BcBigNumber left, BcBigNumber right) {
        return getBcBigNumber(left, right);
    }

    @TruffleBoundary
    private BcBigNumber getBcBigNumber(BcBigNumber left, BcBigNumber right) {
        return left.pow(right);
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw BcException.typeError(this, left, right);
    }
}
