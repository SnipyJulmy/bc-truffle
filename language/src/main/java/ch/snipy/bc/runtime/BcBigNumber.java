package ch.snipy.bc.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.MessageResolution;
import com.oracle.truffle.api.interop.Resolve;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;

import java.math.BigDecimal;

@MessageResolution(receiverType = BcBigNumber.class)
public final class BcBigNumber implements TruffleObject, Comparable<BcBigNumber> {

    private final BigDecimal value;

    public BcBigNumber(BigDecimal value) {
        this.value = value;
    }

    static boolean isInstance(TruffleObject obj) {
        return obj instanceof BcBigNumber;
    }

    public BigDecimal value() {
        return value;
    }

    @TruffleBoundary
    public int compareTo(BcBigNumber o) {
        return value.compareTo(o.value());
    }

    @Override
    public ForeignAccess getForeignAccess() {
        return BcBigNumberForeign.ACCESS;
    }

    @Override
    @TruffleBoundary
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BcBigNumber)
            return value.equals(((BcBigNumber) obj).value());
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Resolve(message = "UNBOX")
    abstract static class UnboxBigNode extends Node {
        Object access(BcBigNumber obj) {
            return obj.value.doubleValue();
        }
    }

    @Resolve(message = "IS_BOXED")
    abstract static class IsBoxedBigNode extends Node {
        @SuppressWarnings("unused")
        Object access(BcBigNumber obj) {
            return true;
        }
    }

}
