package ch.snipy.bc.runtime;

import ch.snipy.bc.BcException;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.nodes.Node;

public class BcUndefinedNameException extends BcException {
    private static final long serial = 1L;

    private BcUndefinedNameException(String msg, Node node) {
        super(msg, node);
    }

    @TruffleBoundary
    public static BcUndefinedNameException undefinedFunction(Node location, Object name) {
        throw new BcUndefinedNameException("Undefined function : " + name, location);
    }
}
