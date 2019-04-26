package ch.snipy.bc.builtins;

import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import java.io.PrintWriter;
import java.util.Arrays;

@NodeInfo(shortName = "print")
public abstract class BcPrintlnBuiltin extends BcBuiltinNode {

    @TruffleBoundary
    private static void doPrint(PrintWriter out, Object[] value) {
        out.println(Arrays.toString(value));
    }

    @TruffleBoundary
    private static void doPrint(PrintWriter out, Object value) {
        out.println(value);
    }

    @TruffleBoundary
    private static void doPrint(PrintWriter out, BcBigNumber value) {
        out.println(value);
    }

    @TruffleBoundary
    private static void doPrint(PrintWriter out, String value) {
        out.println(value);
    }

    @Specialization
    public Object[] print(Object[] args) {
        doPrint(getContext().getOutput(), args);
        return args;
    }

    @Specialization
    public BcBigNumber print(BcBigNumber value) {
        doPrint(getContext().getOutput(), value);
        return value;
    }

    @Specialization
    public String print(String value) {
        doPrint(getContext().getOutput(), value);
        return value;
    }

    @Specialization
    public Object print(Object value) {
        doPrint(getContext().getOutput(), value);
        return value;
    }

}
