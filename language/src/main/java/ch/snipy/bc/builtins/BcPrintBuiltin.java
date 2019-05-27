package ch.snipy.bc.builtins;

import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import java.io.PrintWriter;
import java.util.Arrays;

@SuppressWarnings("WeakerAccess")
@NodeInfo(shortName = "print", description = "builtin function for printing")
public abstract class BcPrintBuiltin extends BcBuiltinNode {

    @TruffleBoundary
    private static void doPrint(PrintWriter out, Object[] value) {
        out.print(Arrays.toString(value));
        out.flush();
    }

    @TruffleBoundary
    private static void doPrint(PrintWriter out, Object value) {
        out.print(value);
        out.flush();
    }

    @TruffleBoundary
    private static void doPrint(PrintWriter out, BcBigNumber value) {
        out.print(value);
        out.flush();
    }

    @TruffleBoundary
    private static void doPrint(PrintWriter out, String value) {
        String res = value.replaceAll("\\\\n", "\n");
        out.print(res);
        out.flush();
    }

    @Specialization
    protected boolean print(boolean value) {
        doPrint(getContext().getOutput(), value ? 1 : 0);
        return value;
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
