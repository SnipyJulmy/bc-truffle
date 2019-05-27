package ch.snipy.bc.builtins.mathlib;

import ch.snipy.bc.builtins.BcBuiltinNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@SuppressWarnings("WeakerAccess")
@NodeInfo(shortName = "s", description = "builtin for the sinus function")
public abstract class BcSinBuiltin extends BcBuiltinNode {

    @Specialization
    public double sin(long arg) {
        return doSin(arg);
    }

    @Specialization
    public BcBigNumber sin(BcBigNumber arg) {
        return arg.sin();
    }

    @TruffleBoundary
    private double doSin(double arg) {
        return Math.sin(arg);
    }
}
