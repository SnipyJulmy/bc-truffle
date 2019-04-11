package ch.snipy.bc.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.MessageResolution;
import com.oracle.truffle.api.interop.Resolve;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;

import java.math.BigDecimal;
import java.math.RoundingMode;

@MessageResolution(receiverType = BcBigNumber.class)
public final class BcBigNumber implements TruffleObject, Comparable<BcBigNumber> {

    public static final BcBigNumber ZERO = new BcBigNumber(BigDecimal.ZERO);
    public static final BcBigNumber ONE = new BcBigNumber(BigDecimal.ONE);
    public static final BcBigNumber FALSE = ZERO;
    public static final BcBigNumber TRUE = ONE;

    private final BigDecimal value;

    public BcBigNumber(BigDecimal value) {
        this.value = value;
    }

    public BcBigNumber(String value) {
        this.value = new BigDecimal(value);
    }

    public BcBigNumber(double value) {
        this.value = new BigDecimal(value);
    }

    public BcBigNumber(int value) {
        this.value = new BigDecimal(value)
                .setScale(0, RoundingMode.UNNECESSARY);
    }

    public static boolean isInstance(TruffleObject obj) {
        return obj instanceof BcBigNumber;
    }

    public static BcBigNumber valueOf(boolean value) {
        return value ? TRUE : FALSE;
    }

    public static BcBigNumber valueOf(BigDecimal value) {
        return new BcBigNumber(value);
    }

    public static BcBigNumber valueOf(int value) {
        return new BcBigNumber(value);
    }

    public BigDecimal getValue() {
        return value;
    }

    @TruffleBoundary
    public int compareTo(BcBigNumber o) {
        return value.compareTo(o.getValue());
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
            return value.equals(((BcBigNumber) obj).getValue());
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public boolean asBoolean() {
        return !(this.value.equals(BigDecimal.ZERO));
    }

    public BcBigNumber add(BcBigNumber that) {
        return valueOf(value.add(that.value));
    }

    public BcBigNumber divide(BcBigNumber right) {
        return valueOf(this.value.divide(right.value, BigDecimal.ROUND_FLOOR));
    }

    public BcBigNumber subtract(BcBigNumber right) {
        return valueOf(this.value.subtract(right.value));
    }

    public BcBigNumber multiply(BcBigNumber right) {
        return valueOf(this.value.multiply(right.value));
    }

    public BcBigNumber remainder(BcBigNumber right) {
        return valueOf(this.value.remainder(right.value));
    }

    public BcBigNumber negate() {
        return valueOf(this.value.negate());
    }

    // fixme : int value verification
    public BcBigNumber pow(BcBigNumber right) {
        int exponent = right.value.intValue();
        return valueOf(this.value.pow(exponent));
    }

    @Resolve(message = "UNBOX")
    abstract static class UnboxBigNode extends Node {
        Object access(BcBigNumber obj) {
            return obj.value.doubleValue();
        }
    }

    // Math

    @Resolve(message = "IS_BOXED")
    abstract static class IsBoxedBigNode extends Node {
        @SuppressWarnings("unused")
        Object access(BcBigNumber obj) {
            return true;
        }
    }
}
