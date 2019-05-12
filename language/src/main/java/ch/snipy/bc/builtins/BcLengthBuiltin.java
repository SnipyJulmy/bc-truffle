package ch.snipy.bc.builtins;

import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "length")
public abstract class BcLengthBuiltin extends BcBuiltinNode {

    @Specialization
    protected long length(boolean value) {
        return 1;
    }

    @Specialization
    protected long length(long value) {
        return doLength(value);
    }

    @Specialization
    protected long length(double value) {
        return doLength(value);
    }

    @Specialization
    public BcBigNumber length(BcBigNumber arg) {
        return arg.length();
    }

    @TruffleBoundary
    private long doLength(long value) {
        return (value + "").length() - (value < 0 ? 1 : 0);
    }

    @TruffleBoundary
    private long doLength(double value) {
        return (value + "").length() - (value < 0 ? 1 : 0);
    }
}
