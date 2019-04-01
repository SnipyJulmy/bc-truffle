package ch.snipy.bc.builtins;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import java.io.PrintWriter;
import java.math.BigDecimal;

@NodeInfo(shortName = "print")
public abstract class BcPrintlnBuiltin extends BcBuiltinNode {

    @TruffleBoundary
    private static void doPrint(PrintWriter out, Object value) {
        out.println(value);
    }

    @TruffleBoundary
    private static void doPrint(PrintWriter out, BigDecimal value) {
        out.println(value);
    }

    @TruffleBoundary
    private static void doPrint(PrintWriter out, String value) {
        out.println(value);
    }

    @Specialization
    public String print(String value) {
        doPrint(getContext().getOutput(), value);
        return value;
    }

    @Specialization
    public BigDecimal print(BigDecimal value) {
        doPrint(getContext().getOutput(), value.toString());
        return value;
    }

    @Specialization
    public Object print(Object value) {
        doPrint(getContext().getOutput(), value);
        return value;
    }

}
