package ch.snipy.bc.builtins;

import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@SuppressWarnings("WeakerAccess")
@NodeInfo(shortName = "length", description = "builtin for the length function, which return the length of a number")
public abstract class BcLengthBuiltin extends BcBuiltinNode {

    @Specialization
    protected long length(long value) {
        return doLength(value);
    }

    @Specialization
    protected BcBigNumber length(BcBigNumber arg) {
        return arg.length();
    }

    @TruffleBoundary
    private long doLength(long value) {
        return (value + "").length() - (value < 0 ? 1 : 0);
    }
}
