package ch.snipy.bc.builtins.mathlib;

import ch.snipy.bc.builtins.BcBuiltinNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "s")
public abstract class BcSinBuiltin extends BcBuiltinNode {

    @Specialization
    protected double sin(boolean value) {
        return doSin(value ? 1.0 : 0.0);
    }

    @Specialization
    public double sin(long arg) {
        return doSin(arg);
    }

    @Specialization
    public double sin(double arg) {
        return doSin(arg);
    }

    @Specialization
    public BcBigNumber sin(BcBigNumber arg) {
        return arg.cos();
    }

    @TruffleBoundary
    private double doSin(double arg) {
        return Math.sin(arg);
    }
}
