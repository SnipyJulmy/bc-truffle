package ch.snipy.bc.node;

import ch.snipy.bc.runtime.BcBigNumber;
import ch.snipy.bc.runtime.BcNull;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeCast;
import com.oracle.truffle.api.dsl.TypeCheck;
import com.oracle.truffle.api.dsl.TypeSystem;

@TypeSystem({
        long.class,
        double.class,
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

    @ImplicitCast
    @TruffleBoundary
    public static long castLong(boolean value) {
        return value ? 1 : 0;
    }

    @ImplicitCast
    @TruffleBoundary
    public static BcBigNumber castBigNumber(long value) {
        return BcBigNumber.valueOf(value);
    }

    @ImplicitCast
    @TruffleBoundary
    public static BcBigNumber castBigNumber(double value) {
        return BcBigNumber.valueOf(value);
    }
}
