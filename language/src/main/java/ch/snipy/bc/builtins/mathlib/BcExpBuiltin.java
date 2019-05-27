package ch.snipy.bc.builtins.mathlib;

import ch.snipy.bc.builtins.BcBuiltinNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@SuppressWarnings("WeakerAccess")
@NodeInfo(shortName = "exp", description = "builtin for the exponential function")
public abstract class BcExpBuiltin extends BcBuiltinNode {

    @Specialization()
    protected double exp(long value) {
        return doExp(value);
    }

    @Specialization
    protected BcBigNumber exp(BcBigNumber arg) {
        return arg.exp();
    }

    @TruffleBoundary
    private double doExp(double value) {
        return Math.exp(value);
    }
}
