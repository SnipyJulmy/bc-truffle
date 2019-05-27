package ch.snipy.bc.builtins.mathlib;

import ch.snipy.bc.builtins.BcBuiltinNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@SuppressWarnings("WeakerAccess")
@NodeInfo(shortName = "ln", description = "builtin for the natural logarithm function")
public abstract class BcLnBuiltin extends BcBuiltinNode {

    @Specialization
    public double ln(long value) {
        return doLn(value);
    }

    @Specialization
    public BcBigNumber ln(BcBigNumber value) {
        return value.ln();
    }

    @TruffleBoundary
    private double doLn(double value) {
        return Math.log(value);
    }
}
