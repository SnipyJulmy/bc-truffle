package ch.snipy.bc.builtins;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "nanotime")
public abstract class BcNanoBuiltin extends BcBuiltinNode {

    @Specialization
    public String printNanotime() {
        getPrintln();
        return "";
    }

    @TruffleBoundary
    private void getPrintln() {
        System.out.println(System.nanoTime());
    }
}
