package ch.snipy.bc.node;

import ch.snipy.bc.runtime.BcBigNumber;
import ch.snipy.bc.runtime.BcNull;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeCast;
import com.oracle.truffle.api.dsl.TypeCheck;
import com.oracle.truffle.api.dsl.TypeSystem;

@SuppressWarnings("WeakerAccess")
@TypeSystem({
        long.class,
        String.class
})
public abstract class BcTypes {

    @TypeCheck(BcNull.class)
    public static boolean isNull(Object value) {
        return value == BcNull.SINGLETON;
    }

    @TypeCast(BcNull.class)
    public static BcNull asBcNull(Object value) {
        assert isNull(value);
        return BcNull.SINGLETON;
    }

    // implicit cast are here for automatically converting "lower" value to upper
    // ones, in particularly for the specialization

    @ImplicitCast
    @TruffleBoundary
    public static long castLong(int value) {
        return value;
    }

    @ImplicitCast
    @TruffleBoundary
    public static BcBigNumber castBigNumber(int value) {
        return BcBigNumber.valueOf(value);
    }

    @ImplicitCast
    @TruffleBoundary
    public static BcBigNumber castBigNumber(long value) {
        return BcBigNumber.valueOf(value);
    }
}
