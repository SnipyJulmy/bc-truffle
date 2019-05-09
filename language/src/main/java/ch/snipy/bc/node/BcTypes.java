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
        BcBigNumber.class,
        String.class,
        Object[].class
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

    /*
        Implicit casting of long value to BigNumber
     */
    @ImplicitCast
    @TruffleBoundary
    public static BcBigNumber castBigNumber(long value) {
        return BcBigNumber.valueOf(value);
    }
}
