package ch.snipy.bc.runtime;

import ch.obermuhlner.math.big.BigDecimalMath;
import ch.snipy.bc.BcLanguage;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@ExportLibrary(InteropLibrary.class)
public final class BcBigNumber implements TruffleObject, Comparable<BcBigNumber> {

    // Some usefull constant
    public static final BcBigNumber ZERO = new BcBigNumber(BigDecimal.ZERO);
    public static final BcBigNumber ONE = new BcBigNumber(BigDecimal.ONE);
    public static final BcBigNumber FALSE = ZERO;
    public static final BcBigNumber TRUE = ONE;

    public static final BigDecimal PI = new BigDecimal(Math.PI);
    public static final BigDecimal E = new BigDecimal(Math.E);


    private final BigDecimal value;

    private static int getScale() {
        return BcLanguage.getCurrentContext().getScale();
    }

    private static RoundingMode getRoundingMode() {
        return BcLanguage.getCurrentContext().getRoundingMode();
    }

    private static MathContext getMathContext() {
        return BcLanguage.getCurrentContext().getMathContext();
    }

    @TruffleBoundary
    private BigDecimal scales(BigDecimal value) {
        if (getScale() == 0)
            return value.setScale(0, RoundingMode.UNNECESSARY);
        return value.setScale(getScale(), getRoundingMode());
    }

    @TruffleBoundary
    public BcBigNumber(BigDecimal value) {
        this.value = scales(value);
    }

    @TruffleBoundary
    public BcBigNumber(String value) {
        this.value = scales(new BigDecimal(value));
    }

    @TruffleBoundary
    public BcBigNumber(double value) {
        this.value = scales(new BigDecimal(value));
    }

    @TruffleBoundary
    public BcBigNumber(int value) {
        this.value = scales(new BigDecimal(value));
    }

    public static boolean isInstance(TruffleObject obj) {
        return obj instanceof BcBigNumber;
    }

    @TruffleBoundary
    public static BcBigNumber valueOf(boolean value) {
        return value ? TRUE : FALSE;
    }

    @TruffleBoundary
    public static BcBigNumber valueOf(BigDecimal value) {
        return new BcBigNumber(value);
    }

    @TruffleBoundary
    public static BcBigNumber valueOf(int value) {
        return new BcBigNumber(value);
    }

    @TruffleBoundary
    public static BcBigNumber valueOf(double value) {
        return new BcBigNumber(value);
    }

    public BigDecimal getValue() {
        return value;
    }

    // fixme check if it fits ? -> generate warning
    public int intValue() {
        return value.intValue();
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

    @TruffleBoundary
    public BcBigNumber add(BcBigNumber that) {
        return valueOf(value.add(that.value));
    }

    @TruffleBoundary
    public BcBigNumber divide(BcBigNumber right) {
        return valueOf(this.value.divide(right.value, BigDecimal.ROUND_FLOOR));
    }

    @TruffleBoundary
    public BcBigNumber subtract(BcBigNumber right) {
        return valueOf(this.value.subtract(right.value));
    }

    @TruffleBoundary
    public BcBigNumber multiply(BcBigNumber right) {
        return valueOf(this.value.multiply(right.value));
    }

    @TruffleBoundary
    public BcBigNumber remainder(BcBigNumber right) {
        return valueOf(this.value.remainder(right.value));
    }

    @TruffleBoundary
    public BcBigNumber negate() {
        return valueOf(this.value.negate());
    }

    // fixme : int value verification
    @TruffleBoundary
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

    @TruffleBoundary
    public BcBigNumber sin() {
        return valueOf(BigDecimalMath.sin(this.value, getMathContext()));
    }

    @TruffleBoundary
    public BcBigNumber cos() {
        return valueOf(BigDecimalMath.cos(this.value, getMathContext()));
    }

    @TruffleBoundary
    public BcBigNumber atan() {
        return valueOf(BigDecimalMath.atan(this.value, getMathContext()));
    }

    @TruffleBoundary
    public BcBigNumber ln() {
        return valueOf(BigDecimalMath.log(this.value, getMathContext()));
    }

    @TruffleBoundary
    public BcBigNumber exp() {
        return valueOf(BigDecimalMath.exp(this.value, getMathContext()));
    }

    // TODO bessel function of order n
}
