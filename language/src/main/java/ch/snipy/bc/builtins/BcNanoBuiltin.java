package ch.snipy.bc.builtins;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@SuppressWarnings("WeakerAccess")
@NodeInfo(shortName = "nanotime", description = "builtin function to print the current nanotime")
public abstract class BcNanoBuiltin extends BcBuiltinNode {

    @Specialization
    protected String printNanotime() {
        getPrintln();
        return "";
    }

    @TruffleBoundary
    private void getPrintln() {
        getContext().getOutput().println(System.nanoTime());
    }
}
