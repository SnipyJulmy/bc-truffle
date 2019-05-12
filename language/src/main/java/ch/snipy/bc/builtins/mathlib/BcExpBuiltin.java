package ch.snipy.bc.builtins.mathlib;

import ch.snipy.bc.builtins.BcBuiltinNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "exp")
public abstract class BcExpBuiltin extends BcBuiltinNode {

    @Specialization()
    protected double exp(boolean value) {
        return doExp(value ? 1.0 : 0.0);
    }

    @Specialization()
    protected double exp(long value) {
        return doExp(value);
    }

    @Specialization()
    protected double exp(double value) {
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
