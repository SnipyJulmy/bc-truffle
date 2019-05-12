package ch.snipy.bc.builtins.mathlib;

import ch.snipy.bc.builtins.BcBuiltinNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "c")
public abstract class BcCosBuiltin extends BcBuiltinNode {

    @Specialization
    protected double cos(boolean value) {
        return doCos(value ? 1.0 : 0.0);
    }

    @Specialization
    public double cos(long arg) {
        return doCos(arg);
    }

    @Specialization
    public double cos(double arg) {
        return doCos(arg);
    }

    @Specialization
    public BcBigNumber cos(BcBigNumber arg) {
        return arg.cos();
    }

    @TruffleBoundary
    private double doCos(double arg) {
        return Math.cos(arg);
    }
}
