package ch.snipy.bc.builtins.mathlib;

import ch.snipy.bc.builtins.BcBuiltinNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "a")
public abstract class BcAtanBuiltin extends BcBuiltinNode {

    @Specialization
    protected double atan(boolean value) {
        return doAtan(value ? 1.0 : 0.0);
    }

    @Specialization
    protected double atan(long value) {
        return doAtan(value);
    }

    @Specialization
    protected double atan(double value) {
        return doAtan(value);
    }

    @Specialization
    protected BcBigNumber atan(BcBigNumber arg) {
        return arg.atan();
    }

    @TruffleBoundary
    private double doAtan(double value) {
        return Math.atan(value);
    }
}
