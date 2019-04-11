package ch.snipy.bc.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import java.math.BigDecimal;
import java.math.RoundingMode;

@ExportLibrary(InteropLibrary.class)
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

    public boolean booleanValue() {
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

    @ExportMessage
    boolean isNumber() {
        return fitsInLong();
    }

    @ExportMessage
    @TruffleBoundary
    boolean fitsInByte() {
        return value.unscaledValue().bitLength() - value.scale() < 8;
    }

    @ExportMessage
    @TruffleBoundary
    boolean fitsInShort() {
        return value.unscaledValue().bitLength() - value.scale() < 16;
    }

    @ExportMessage
    @TruffleBoundary
    boolean fitsInInt() {
        return value.unscaledValue().bitLength() - value.scale() < 32;
    }

    @ExportMessage
    @TruffleBoundary
    boolean fitsInLong() {
        return value.unscaledValue().bitLength() - value.scale() < 64;
    }

    @ExportMessage
    @TruffleBoundary
    boolean fitsInFloat() {
        return fitsInInt(); // fixme check safe float range
    }

    @ExportMessage
    @TruffleBoundary
    boolean fitsInDouble() {
        return fitsInLong(); // fixme check safe double range
    }

    @ExportMessage
    @TruffleBoundary
    double asDouble() throws UnsupportedMessageException {
        if (fitsInDouble()) {
            return value.doubleValue();
        } else {
            throw UnsupportedMessageException.create();
        }
    }

    @ExportMessage
    @TruffleBoundary
    long asLong() throws UnsupportedMessageException {
        if (fitsInLong()) {
            return value.longValue();
        } else {
            throw UnsupportedMessageException.create();
        }
    }

    @ExportMessage
    @TruffleBoundary
    byte asByte() throws UnsupportedMessageException {
        if (fitsInByte()) {
            return value.byteValue();
        } else {
            throw UnsupportedMessageException.create();
        }
    }

    @ExportMessage
    @TruffleBoundary
    int asInt() throws UnsupportedMessageException {
        if (fitsInInt()) {
            return value.intValue();
        } else {
            throw UnsupportedMessageException.create();
        }
    }

    @ExportMessage
    @TruffleBoundary
    float asFloat() throws UnsupportedMessageException {
        if (fitsInFloat()) {
            return value.floatValue();
        } else {
            throw UnsupportedMessageException.create();
        }
    }

    @ExportMessage
    @TruffleBoundary
    short asShort() throws UnsupportedMessageException {
        if (fitsInShort()) {
            return value.shortValue();
        } else {
            throw UnsupportedMessageException.create();
        }
    }
}
