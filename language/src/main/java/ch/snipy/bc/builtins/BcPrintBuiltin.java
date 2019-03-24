package ch.snipy.bc.builtins;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import java.io.PrintWriter;

@NodeInfo(shortName = "print")
public abstract class BcPrintBuiltin extends BcBuiltinNode {

    @Specialization
    public String print(String value) {
        doPrint(getContext().getOutput(), value);
        return value;
    }

    @Specialization
    public long print(long value) {
        doPrint(getContext().getOutput(), value);
        return value;
    }

    @Specialization
    public Object print(Object value) {
        doPrint(getContext().getOutput(), value);
        return value;
    }

    @TruffleBoundary
    private static void doPrint(PrintWriter out, Object value) {
        out.println(value);
    }

    @TruffleBoundary
    private static void doPrint(PrintWriter out, double value) {
        out.println(value);
    }

    @TruffleBoundary
    private static void doPrint(PrintWriter out, String value) {
        out.println(value);
    }

}
