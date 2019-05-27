package ch.snipy.bc.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

/**
 * In bc, the null value is represented by 0.
 * For example, if we just write
 * print a
 * without declared a yet, then it will print 0.
 */
@ExportLibrary(InteropLibrary.class)
@SuppressWarnings("static-method")
public final class BcNull implements TruffleObject {
    public static final BcNull SINGLETON = new BcNull();

    private BcNull() {
    }

    @Override
    public String toString() {
        return "0";
    }

    @ExportMessage
    boolean isNull() {
        return true;
    }
}
