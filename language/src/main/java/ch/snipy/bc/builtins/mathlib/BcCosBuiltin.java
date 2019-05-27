package ch.snipy.bc.builtins.mathlib;

import ch.snipy.bc.builtins.BcBuiltinNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@SuppressWarnings("WeakerAccess")
@NodeInfo(shortName = "c", description = "builtin for the cosinus function")
public abstract class BcCosBuiltin extends BcBuiltinNode {

    @Specialization
    public double cos(long arg) {
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
